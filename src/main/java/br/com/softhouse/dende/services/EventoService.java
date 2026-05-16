package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.evento.*;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.evento.*;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorNaoEncontradoException;
import br.com.softhouse.dende.mapper.EventoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class EventoService {

    private final Repositorio repositorio = Repositorio.getInstance();

    public StatusEventoDto cadastrarEvento(Long organizadorId, CadastrarEventoDto dto) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        validarCadastro(dto);

        Evento evento = EventoMapper.toModel(dto);
        if (dto.eventoPrincipalId() != null) {
            Evento principal = repositorio.buscarEventoPorId(dto.eventoPrincipalId())
                    .orElseThrow(() -> new EventoPrincipalNaoEncontradoException("Evento principal não encontrado."));
            evento.setEventoPrincipal(principal);
        }

        organizador.addEvento(evento);
        repositorio.salvarEvento(organizador, evento);
        return EventoMapper.toStatusEventoDto("Evento criado com sucesso!", evento);
    }

    public StatusEventoDto atualizarEvento(Long organizadorId, Long eventoId, AtualizarEventoDto dto) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        Evento evento = organizador.getEventos().stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.isEventoAtivo()) throw new EventoInativoException("Evento inativo não pode ser alterado.");

        validarAtualizacao(dto);
        EventoMapper.updateModel(evento, dto);
        if (dto.eventoPrincipalId() != null) {
            Evento principal = repositorio.buscarEventoPorId(dto.eventoPrincipalId())
                    .orElseThrow(() -> new EventoPrincipalNaoEncontradoException("Evento principal não encontrado."));
            evento.setEventoPrincipal(principal);
        }
        return EventoMapper.toStatusEventoDto("Evento atualizado com sucesso!", evento);
    }

    public StatusEventoDto ativarEvento(Long organizadorId, Long eventoId) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        Evento evento = organizador.getEventos().stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (evento.isEventoAtivo()) throw new EventoJaAtivoException("Evento já está ativo.");

        validarDatasEvento(evento.getDataInicio(), evento.getDataFim());
        evento.setEventoAtivo(true);
        evento.setIngressosDisponiveis(evento.getCapacidadeMaxima());
        return EventoMapper.toStatusEventoDto("Evento ativado!", evento);
    }

    public StatusEventoDto desativarEvento(Long organizadorId, Long eventoId) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        Evento evento = organizador.getEventos().stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .orElseThrow(() -> new EventoNaoEncontradoException("Evento não encontrado."));
        if (!evento.isEventoAtivo()) throw new EventoJaInativoException("Evento já está inativo.");

        // Cancela todos os ingressos e desvincula dos usuários
        for (Ingresso ingresso : evento.getIngressos()) {
            if (!ingresso.isCancelado()) {
                ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);
                if (evento.isEventoEstorno()) {
                    double valorEstorno = ingresso.getValorPago() * (1 - evento.getTaxaCancelamento() / 100.0);
                    ingresso.setValorEstornado(valorEstorno);
                } else {
                    ingresso.setValorEstornado(0.0);
                }
                // Remove do usuário
                Usuario usuario = ingresso.getUsuario();
                if (usuario != null) {
                    usuario.removeIngresso(ingresso);
                }
            }
        }
        evento.getIngressos().clear();  // limpa a lista do evento
        evento.setEventoAtivo(false);
        evento.setIngressosDisponiveis(0);
        return EventoMapper.toStatusEventoDto("Evento desativado e ingressos cancelados.", evento);
    }

    public List<EventosOrganizadorDto> listarEventosOrganizador(Long organizadorId) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(organizadorId)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        return organizador.getEventos().stream()
                .map(EventoMapper::toEventosOrganizadorDto)
                .toList();
    }

    public List<FeedEventoDto> listarEventosAtivos() {
        return repositorio.listarEventosAtivos().stream()
                .filter(e -> e.getDataFim().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Evento::getDataInicio).thenComparing(Evento::getNome))
                .map(EventoMapper::toFeedEventoDto)
                .toList();
    }

    private void validarCadastro(CadastrarEventoDto dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Dados do evento inválidos.");
        }
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new DadosInvalidosException("Nome do evento é obrigatório.");
        }
        if (dto.localEvento() == null || dto.localEvento().isBlank()) {
            throw new DadosInvalidosException("Local do evento é obrigatório.");
        }
        if (dto.capacidadeMaxima() <= 0) {
            throw new EventoCapacidadeInvalidaException("Capacidade máxima deve ser maior que zero.");
        }
        if (dto.precoUnitarioIngresso() < 0) {
            throw new EventoPrecoIngressoInvalidoException("Preço do ingresso não pode ser negativo.");
        }
        validarDatasEvento(dto.dataInicio(), dto.dataFim());
    }

    private void validarAtualizacao(AtualizarEventoDto dto) {
        if (dto == null) {
            throw new DadosInvalidosException("Dados de atualização inválidos.");
        }
        if (dto.precoUnitarioIngresso() != null && dto.precoUnitarioIngresso() < 0) {
            throw new EventoPrecoIngressoInvalidoException("Preço do ingresso não pode ser negativo.");
        }
        if (dto.capacidadeMaxima() != null && dto.capacidadeMaxima() < 0) {
            throw new EventoCapacidadeInvalidaException("Capacidade máxima não pode ser negativa.");
        }
        if (dto.taxaCancelamento() != null && dto.taxaCancelamento() < 0) {
            throw new EventoTaxaCancelamentoInvalidaException("Taxa de cancelamento não pode ser negativa.");
        }
    }

    private void validarDatasEvento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new DadosInvalidosException(
                    "Datas do evento são obrigatórias."
            );
        }
        LocalDateTime agora = LocalDateTime.now();
        if (dataInicio.isBefore(agora)) {
            throw new EventoDataInicioInvalidaException(
                    "Data de início inválida."
            );
        }
        if (dataFim.isBefore(agora)) {
            throw new EventoDataFimInvalidaException(
                    "Data de fim inválida."
            );
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new EventoDataFimAnteriorInicioException(
                    "Data final não pode ser anterior à inicial."
            );
        }
        long duracao = Duration.between(dataInicio, dataFim).toMinutes();
        if (duracao < 30) {
            throw new EventoDuracaoInvalidaException(
                    "Evento deve ter no mínimo 30 minutos."
            );
        }
    }
}