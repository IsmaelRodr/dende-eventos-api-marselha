package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.evento.AtualizarEventoDto;
import br.com.softhouse.dende.dto.evento.CadastrarEventoDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.evento.EventoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.evento.EventoJaAtivoException;
import br.com.softhouse.dende.exceptions.evento.EventoJaInativoException;
import br.com.softhouse.dende.exceptions.evento.EventoInativoException;
import br.com.softhouse.dende.exceptions.evento.EventoDuracaoInvalidaException;
import br.com.softhouse.dende.exceptions.evento.EventoDataInicioInvalidaException;
import br.com.softhouse.dende.exceptions.evento.EventoDataFimInvalidaException;
import br.com.softhouse.dende.exceptions.evento.EventoDataFimAnteriorInicioException;
import br.com.softhouse.dende.exceptions.evento.EventoPrecoIngressoInvalidoException;
import br.com.softhouse.dende.exceptions.evento.EventoCapacidadeInvalidaException;
import br.com.softhouse.dende.exceptions.evento.EventoTaxaCancelamentoInvalidaException;
import br.com.softhouse.dende.exceptions.evento.EventoPrincipalNaoEncontradoException;

import br.com.softhouse.dende.exceptions.organizador.OrganizadorNaoEncontradoException;

import br.com.softhouse.dende.mapper.EventoMapper;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;

import br.com.softhouse.dende.repositories.Repositorio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EventoService {

    private final Repositorio repositorio;

    public EventoService() {
        this.repositorio = Repositorio.getInstance();
    }

    public Evento cadastrar(
            Long organizadorId,
            CadastrarEventoDto dto
    ) {

        if (organizadorId == null || organizadorId <= 0) {

            throw new DadosInvalidosException(
                    "ID do organizador inválido."
            );
        }

        if (dto == null) {

            throw new DadosInvalidosException(
                    "Dados do evento inválidos."
            );
        }

        Organizador organizador =
                repositorio.buscarOrganizadorPorId(
                        organizadorId
                );

        if (organizador == null) {

            throw new OrganizadorNaoEncontradoException(
                    "Organizador não encontrado."
            );
        }

        Evento evento = EventoMapper.toModel(dto);

        if (evento == null) {

            throw new DadosInvalidosException(
                    "Falha ao converter os dados do evento."
            );
        }

        if (evento.getNome() == null ||
                evento.getNome().isBlank()) {

            throw new DadosInvalidosException(
                    "Nome do evento é obrigatório."
            );
        }

        if (evento.getLocalEvento() == null ||
                evento.getLocalEvento().isBlank()) {

            throw new DadosInvalidosException(
                    "Local do evento é obrigatório."
            );
        }

        if (evento.getCapacidadeMaxima() <= 0) {

            throw new EventoCapacidadeInvalidaException(
                    "Capacidade máxima inválida."
            );
        }

        if (evento.getPrecoUnitarioIngresso() < 0) {

            throw new EventoPrecoIngressoInvalidoException(
                    "Preço do ingresso inválido."
            );
        }

        validarDatasEvento(
                evento.getDataInicio(),
                evento.getDataFim()
        );

        if (dto.eventoPrincipalId() != null) {

            Evento eventoPrincipal =
                    repositorio.buscarEvento(
                            dto.eventoPrincipalId()
                    );

            if (eventoPrincipal == null) {

                throw new EventoPrincipalNaoEncontradoException(
                        "Evento principal não encontrado."
                );
            }

            evento.setEventoPrincipal(
                    eventoPrincipal
            );
        }

        evento.setOrganizador(organizador);

        repositorio.salvarEvento(
                organizadorId,
                evento
        );

        return evento;
    }

    public Evento atualizar(
            Long organizadorId,
            Long eventoId,
            AtualizarEventoDto dto
    ) {

        if (organizadorId == null || organizadorId <= 0 ||
                eventoId == null || eventoId <= 0) {

            throw new DadosInvalidosException(
                    "IDs inválidos."
            );
        }

        Evento eventoExistente =
                buscarEventoDoOrganizador(
                        organizadorId,
                        eventoId
                );

        if (eventoExistente == null) {

            throw new EventoNaoEncontradoException(
                    "Evento não encontrado."
            );
        }

        if (!eventoExistente.isEventoAtivo()) {

            throw new EventoInativoException(
                    "Evento inativo não pode ser alterado."
            );
        }

        Evento eventoAtualizado =
                EventoMapper.toModel(dto);

        if (eventoAtualizado == null) {

            throw new DadosInvalidosException(
                    "Dados de atualização inválidos."
            );
        }

        if (dto.precoUnitarioIngresso() != null &&
                dto.precoUnitarioIngresso() < 0) {

            throw new EventoPrecoIngressoInvalidoException(
                    "Preço do ingresso não pode ser negativo."
            );
        }

        if (dto.capacidadeMaxima() != null &&
                dto.capacidadeMaxima() < 0) {

            throw new EventoCapacidadeInvalidaException(
                    "Capacidade máxima inválida."
            );
        }

        if (dto.taxaCancelamento() != null &&
                dto.taxaCancelamento() < 0) {

            throw new EventoTaxaCancelamentoInvalidaException(
                    "Taxa de cancelamento inválida."
            );
        }

        if (dto.eventoPrincipalId() != null) {

            Evento principal =
                    repositorio.buscarEvento(
                            dto.eventoPrincipalId()
                    );

            if (principal == null) {

                throw new EventoPrincipalNaoEncontradoException(
                        "Evento principal não encontrado."
                );
            }

            eventoAtualizado.setEventoPrincipal(
                    principal
            );
        }

        if (eventoAtualizado.getDataInicio() != null &&
                eventoAtualizado.getDataFim() != null) {

            validarDatasEvento(
                    eventoAtualizado.getDataInicio(),
                    eventoAtualizado.getDataFim()
            );
        }

        repositorio.atualizarEvento(
                organizadorId,
                eventoAtualizado,
                eventoId
        );

        return buscarEventoDoOrganizador(
                organizadorId,
                eventoId
        );
    }

    public void ativar(
            Long organizadorId,
            Long eventoId
    ) {

        Evento evento =
                buscarEventoDoOrganizador(
                        organizadorId,
                        eventoId
                );

        if (evento == null) {

            throw new EventoNaoEncontradoException(
                    "Evento não encontrado."
            );
        }

        if (evento.isEventoAtivo()) {

            throw new EventoJaAtivoException(
                    "Evento já está ativo."
            );
        }

        validarDatasEvento(
                evento.getDataInicio(),
                evento.getDataFim()
        );

        repositorio.ativarEvento(
                eventoId,
                organizadorId
        );
    }

    public void desativar(
            Long organizadorId,
            Long eventoId
    ) {

        Evento evento =
                buscarEventoDoOrganizador(
                        organizadorId,
                        eventoId
                );

        if (evento == null) {

            throw new EventoNaoEncontradoException(
                    "Evento não encontrado."
            );
        }

        if (!evento.isEventoAtivo()) {

            throw new EventoJaInativoException(
                    "Evento já está desativado."
            );
        }

        repositorio.desativarEvento(
                eventoId,
                organizadorId
        );
    }

    public List<Evento> listarEventosOrganizador(
            Long organizadorId
    ) {

        if (organizadorId == null || organizadorId <= 0) {

            throw new DadosInvalidosException(
                    "ID do organizador inválido."
            );
        }

        Organizador organizador =
                repositorio.buscarOrganizadorPorId(
                        organizadorId
                );

        if (organizador == null) {

            throw new OrganizadorNaoEncontradoException(
                    "Organizador não encontrado."
            );
        }

        return repositorio.listarEventoPorOrganizador(
                organizadorId
        );
    }

    public List<Evento> listarEventosAtivos() {

        return repositorio.listarEventoAtivos();
    }

    private Evento buscarEventoDoOrganizador(
            Long organizadorId,
            Long eventoId
    ) {

        return repositorio
                .listarEventoPorOrganizador(
                        organizadorId
                )
                .stream()
                .filter(
                        e -> e.getId().equals(eventoId)
                )
                .findFirst()
                .orElse(null);
    }

    private void validarDatasEvento(
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {

        if (dataInicio == null || dataFim == null) {

            throw new DadosInvalidosException(
                    "Datas do evento são obrigatórias."
            );
        }

        LocalDateTime agora =
                LocalDateTime.now();

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

        long duracao =
                Duration.between(
                        dataInicio,
                        dataFim
                ).toMinutes();

        if (duracao < 30) {

            throw new EventoDuracaoInvalidaException(
                    "Evento deve ter no mínimo 30 minutos."
            );
        }
    }
}