package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.evento.*;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.evento.*;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorNaoEncontradoException;
import br.com.softhouse.dende.mapper.EventoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.OrganizadorRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class EventoService {

    private final EventoRepository eventoRepository;
    private final OrganizadorRepository organizadorRepository;
    private final IngressoRepository ingressoRepository;

    public EventoService() {
        this.eventoRepository = new EventoRepository();
        this.organizadorRepository = new OrganizadorRepository();
        this.ingressoRepository = new IngressoRepository();
    }

    public StatusEventoDto cadastrarEvento(Long organizadorId, CadastrarEventoDto dto) {
        Organizador organizador = organizadorRepository.findById(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        validarCadastro(dto);
        Evento evento = EventoMapper.toModel(dto);
        if (dto.eventoPrincipalId() != null) {
            Evento principal = eventoRepository.findById(dto.eventoPrincipalId())
                    .orElseThrow(() -> new EventoPrincipalNaoEncontradoException("Evento principal não encontrado."));
            evento.setEventoPrincipal(principal);
        }
        organizador.adicionarEvento(evento);
        eventoRepository.save(evento);
        return EventoMapper.toStatusEventoDto("Evento criado com sucesso!", evento);
    }

    public StatusEventoDto atualizarEvento(Long organizadorId, Long eventoId, AtualizarEventoDto dto) {
        if (!organizadorRepository.existsById(organizadorId)){
                throw  new OrganizadorNaoEncontradoException("Organizador não encontrado.");
        }
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.getOrganizador().getId().equals(organizadorId)) {
            throw new EventoNaoEncontradoException("Evento não pertence ao organizador.");
        }
        if (!evento.isEventoAtivo()) {
            throw new EventoInativoException("Evento inativo não pode ser alterado.");
        }

        validarAtualizacao(dto);
        EventoMapper.updateModel(evento, dto);
        if (dto.eventoPrincipalId() != null) {
            if (dto.eventoPrincipalId().equals(eventoId)) {
                throw new DadosInvalidosException("Um evento não pode ser principal de si mesmo.");
            }
            Evento principal = eventoRepository.findById(dto.eventoPrincipalId())
                    .orElseThrow(() -> new EventoPrincipalNaoEncontradoException("Evento principal não encontrado."));
            if (!principal.getOrganizador().getId().equals(organizadorId)) {
                throw new DadosInvalidosException("O evento principal deve pertencer ao mesmo organizador.");
            }
            evento.setEventoPrincipal(principal);
        }
        eventoRepository.update(evento);
        return EventoMapper.toStatusEventoDto("Evento atualizado com sucesso!", evento);
    }

    public StatusEventoDto ativarEvento(Long organizadorId, Long eventoId) {
        if (!organizadorRepository.existsById(organizadorId)){
            throw  new OrganizadorNaoEncontradoException("Organizador não encontrado.");
        }
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.getOrganizador().getId().equals(organizadorId)) {
            throw new EventoNaoEncontradoException("Evento não pertence ao organizador.");
        }

        validarDatasEvento(evento.getDataInicio(), evento.getDataFim());
        evento.ativar();
        eventoRepository.update(evento);
        return EventoMapper.toStatusEventoDto("Evento ativado!", evento);
    }

    public StatusEventoDto desativarEvento(Long organizadorId, Long eventoId) {
        if (!organizadorRepository.existsById(organizadorId)){
            throw  new OrganizadorNaoEncontradoException("Organizador não encontrado.");
        }
        Evento evento = eventoRepository.findByIdWithIngressos(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.getOrganizador().getId().equals(organizadorId)) {
            throw new EventoNaoEncontradoException("Evento não pertence ao organizador.");
        }

        List<Ingresso> ingressosCancelados = evento.desativar();

        eventoRepository.update(evento);
        for (Ingresso ingresso : ingressosCancelados) {
            ingressoRepository.update(ingresso);
        }
        return EventoMapper.toStatusEventoDto("Evento desativado e ingressos cancelados.", evento);
    }

    public List<EventosOrganizadorDto> listarEventosOrganizador(Long organizadorId) {
        Organizador organizador = organizadorRepository.findByIdWithEventos(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        return organizador.getEventos().stream()
                .map(EventoMapper::toEventosOrganizadorDto)
                .toList();
    }

    public List<FeedEventoDto> listarEventosAtivos() {
        return eventoRepository.findAllAtivos().stream()
                .map(EventoMapper::toFeedEventoDto)
                .toList();
    }

    //validações

    private void validarCadastro(CadastrarEventoDto dto) {
        if (dto == null) throw new DadosInvalidosException("Dados do evento inválidos.");
        if (dto.nome() == null || dto.nome().isBlank())
            throw new DadosInvalidosException("Nome do evento é obrigatório.");
        if (dto.localEvento() == null || dto.localEvento().isBlank())
            throw new DadosInvalidosException("Local do evento é obrigatório.");
        if (dto.capacidadeMaxima() <= 0)
            throw new EventoCapacidadeInvalidaException("Capacidade máxima deve ser maior que zero.");
        if (dto.precoUnitarioIngresso() < 0)
            throw new EventoPrecoIngressoInvalidoException("Preço do ingresso não pode ser negativo.");
        validarDatasEvento(dto.dataInicio(), dto.dataFim());
    }

    private void validarAtualizacao(AtualizarEventoDto dto) {
        if (dto == null) throw new DadosInvalidosException("Dados de atualização inválidos.");
        if (dto.precoUnitarioIngresso() != null && dto.precoUnitarioIngresso() < 0)
            throw new EventoPrecoIngressoInvalidoException("Preço do ingresso não pode ser negativo.");
        if (dto.capacidadeMaxima() != null && dto.capacidadeMaxima() < 0)
            throw new EventoCapacidadeInvalidaException("Capacidade máxima não pode ser negativa.");
        if (dto.taxaCancelamento() != null && dto.taxaCancelamento() < 0)
            throw new EventoTaxaCancelamentoInvalidaException("Taxa de cancelamento não pode ser negativa.");
        // Só valida datas se ambas forem fornecidas
        if (dto.dataInicio() != null && dto.dataFim() != null) {
            validarDatasEvento(dto.dataInicio(), dto.dataFim());
        }
    }

    private void validarDatasEvento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null)
            throw new DadosInvalidosException("Datas do evento são obrigatórias.");
        LocalDateTime agora = LocalDateTime.now();
        if (dataInicio.isBefore(agora))
            throw new EventoDataInicioInvalidaException("Data de início inválida.");
        if (dataFim.isBefore(agora))
            throw new EventoDataFimInvalidaException("Data de fim inválida.");
        if (dataFim.isBefore(dataInicio))
            throw new EventoDataFimAnteriorInicioException("Data final não pode ser anterior à inicial.");
        if (Duration.between(dataInicio, dataFim).toMinutes() < 30)
            throw new EventoDuracaoInvalidaException("Evento deve ter no mínimo 30 minutos.");
    }
}