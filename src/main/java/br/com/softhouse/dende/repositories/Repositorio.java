package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.evento.EventoInativoException;
import br.com.softhouse.dende.exceptions.evento.EventoJaAtivoException;
import br.com.softhouse.dende.exceptions.evento.EventoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.evento.EventoSemIngressosDisponiveisException;

import br.com.softhouse.dende.exceptions.ingresso.CancelamentoNaoPermitidoException;
import br.com.softhouse.dende.exceptions.ingresso.IngressoNaoEncontradoException;

import br.com.softhouse.dende.exceptions.organizador.OrganizadorNaoEncontradoException;

import br.com.softhouse.dende.exceptions.repository.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.repository.OperacaoRepositorioException;
import br.com.softhouse.dende.exceptions.repository.PersistenciaException;

import br.com.softhouse.dende.exceptions.usuario.EmailJaCadastradoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioNaoEncontradoException;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Repositorio {

    private static final Repositorio instance =
            new Repositorio();

    private final Map<Long, Usuario> usuariosComum;
    private final Map<Long, Organizador> organizadores;
    private final Map<Long, List<Evento>> eventos;
    private final Map<Long, List<Ingresso>> ingressosPorUsuario;

    private Long contadorUsuarios = 1L;
    private Long contadorOrganizadores = 1L;
    private Long contadorEventos = 1L;
    private Long contadorIngressos = 1L;

    private Repositorio() {

        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
        this.ingressosPorUsuario = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    // =====================================================
    // USUÁRIOS
    // =====================================================

    public void salvarUsuario(Usuario usuario) {

        if (usuario == null) {

            throw new PersistenciaException(
                    "Usuário inválido."
            );
        }

        if (usuario.getId() == null
                && emailExiste(usuario.getEmail())) {

            throw new EmailJaCadastradoException(
                    "Já existe um usuário com este email."
            );
        }

        if (usuario.getId() == null) {

            usuario.setId(contadorUsuarios++);
        }

        usuariosComum.put(
                usuario.getId(),
                usuario
        );
    }

    public Usuario buscarUsuarioPorId(Long id) {

        Usuario usuario = usuariosComum.get(id);

        if (usuario == null) {

            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        return usuario;
    }

    public void atualizarDadosUsuario(
            Usuario usuarioExistente,
            Usuario novosDados
    ) {

        if (usuarioExistente == null
                || novosDados == null) {

            throw new OperacaoRepositorioException(
                    "Dados inválidos para atualização."
            );
        }

        if (novosDados.getNome() != null) {

            usuarioExistente.setNome(
                    novosDados.getNome()
            );
        }

        if (novosDados.getDataNascimento() != null) {

            usuarioExistente.setDataNascimento(
                    novosDados.getDataNascimento()
            );
        }

        if (novosDados.getSexo() != null) {

            usuarioExistente.setSexo(
                    novosDados.getSexo()
            );
        }

        if (novosDados.getSenha() != null) {

            usuarioExistente.setSenha(
                    novosDados.getSenha()
            );
        }

        usuariosComum.put(
                usuarioExistente.getId(),
                usuarioExistente
        );
    }

    // =====================================================
    // ORGANIZADORES
    // =====================================================

    public void salvarOrganizador(
            Organizador organizador
    ) {

        if (organizador == null) {

            throw new PersistenciaException(
                    "Organizador inválido."
            );
        }

        if (organizador.getId() == null
                && emailExiste(organizador.getEmail())) {

            throw new EmailJaCadastradoException(
                    "Já existe um organizador com este email."
            );
        }

        if (organizador.getId() == null) {

            organizador.setId(
                    contadorOrganizadores++
            );
        }

        organizadores.put(
                organizador.getId(),
                organizador
        );
    }

    public Organizador buscarOrganizadorPorId(
            Long id
    ) {

        Organizador organizador =
                organizadores.get(id);

        if (organizador == null) {

            throw new OrganizadorNaoEncontradoException(
                    "Organizador não encontrado."
            );
        }

        return organizador;
    }

    public void atualizarDadosOrganizador(
            Organizador organizadorExistente,
            Organizador novosDados
    ) {

        if (organizadorExistente == null
                || novosDados == null) {

            throw new OperacaoRepositorioException(
                    "Dados inválidos para atualização."
            );
        }

        if (novosDados.getNome() != null) {

            organizadorExistente.setNome(
                    novosDados.getNome()
            );
        }

        if (novosDados.getDataNascimento() != null) {

            organizadorExistente.setDataNascimento(
                    novosDados.getDataNascimento()
            );
        }

        if (novosDados.getSexo() != null) {

            organizadorExistente.setSexo(
                    novosDados.getSexo()
            );
        }

        if (novosDados.getSenha() != null) {

            organizadorExistente.setSenha(
                    novosDados.getSenha()
            );
        }

        if (novosDados.getEmpresa() != null) {

            organizadorExistente.setEmpresa(
                    novosDados.getEmpresa()
            );
        }

        organizadores.put(
                organizadorExistente.getId(),
                organizadorExistente
        );
    }

    // =====================================================
    // EVENTOS
    // =====================================================

    public void salvarEvento(
            Long organizadorId,
            Evento evento
    ) {

        Organizador organizador =
                buscarOrganizadorPorId(
                        organizadorId
                );

        if (evento == null) {

            throw new PersistenciaException(
                    "Evento inválido."
            );
        }

        if (evento.getId() == null) {

            evento.setId(contadorEventos++);
        }

        organizador.addEvento(evento);

        List<Evento> lista =
                eventos.computeIfAbsent(
                        organizadorId,
                        o -> new ArrayList<>()
                );

        if (!lista.contains(evento)) {

            lista.add(evento);
        }
    }

    public void atualizarEvento(
            Long organizadorId,
            Evento novosDados,
            Long eventoId
    ) {

        Evento eventoExistente =
                buscarEventoPorOrganizador(
                        eventoId,
                        organizadorId
                );

        validarDatasEvento(novosDados);

        if (novosDados.getNome() != null) {

            eventoExistente.setNome(
                    novosDados.getNome()
            );
        }

        if (novosDados.getDescricao() != null) {

            eventoExistente.setDescricao(
                    novosDados.getDescricao()
            );
        }

        if (novosDados.getPaginaWeb() != null) {

            eventoExistente.setPaginaWeb(
                    novosDados.getPaginaWeb()
            );
        }

        if (novosDados.getDataInicio() != null) {

            eventoExistente.setDataInicio(
                    novosDados.getDataInicio()
            );
        }

        if (novosDados.getDataFim() != null) {

            eventoExistente.setDataFim(
                    novosDados.getDataFim()
            );
        }

        if (novosDados.getTipoEvento() != null) {

            eventoExistente.setTipoEvento(
                    novosDados.getTipoEvento()
            );
        }

        if (novosDados.getEventoPrincipal() != null) {

            eventoExistente.setEventoPrincipal(
                    novosDados.getEventoPrincipal()
            );
        }

        if (novosDados.getModalidade() != null) {

            eventoExistente.setModalidade(
                    novosDados.getModalidade()
            );
        }

        if (novosDados.getLocalEvento() != null) {

            eventoExistente.setLocalEvento(
                    novosDados.getLocalEvento()
            );
        }

        eventoExistente.setPrecoUnitarioIngresso(
                novosDados.getPrecoUnitarioIngresso()
        );

        eventoExistente.setTaxaCancelamento(
                novosDados.getTaxaCancelamento()
        );

        eventoExistente.setEventoEstorno(
                novosDados.isEventoEstorno()
        );

        eventoExistente.setCapacidadeMaxima(
                novosDados.getCapacidadeMaxima()
        );
    }

    public Evento buscarEvento(Long eventoId) {

        Evento evento =
                encontrarEvento(eventoId);

        if (evento == null) {

            throw new EventoNaoEncontradoException(
                    "Evento não encontrado."
            );
        }

        return evento;
    }

    public void ativarEvento(
            Long eventoId,
            Long organizadorId
    ) {

        Evento evento =
                buscarEventoPorOrganizador(
                        eventoId,
                        organizadorId
                );

        if (evento.isEventoAtivo()) {

            throw new EventoJaAtivoException(
                    "Evento já está ativo."
            );
        }

        evento.setEventoAtivo(true);

        liberarIngressosEvento(
                eventoId,
                organizadorId
        );
    }

    public void desativarEvento(
            Long eventoId,
            Long organizadorId
    ) {

        Evento evento =
                buscarEventoPorOrganizador(
                        eventoId,
                        organizadorId
                );

        if (!evento.isEventoAtivo()) {

            throw new EventoInativoException(
                    "Evento já está inativo."
            );
        }

        cancelarTodosIngressosEvento(
                eventoId
        );

        evento.setEventoAtivo(false);

        evento.setIngressosDisponiveis(0);
    }

    public List<Evento> listarEventoPorOrganizador(
            Long organizadorId
    ) {

        Organizador organizador =
                buscarOrganizadorPorId(
                        organizadorId
                );

        return organizador.getEventos();
    }

    public List<Evento> listarEventoAtivos() {

        List<Evento> eventosAtivos =
                new ArrayList<>();

        for (Organizador org :
                organizadores.values()) {

            for (Evento evento :
                    org.getEventos()) {

                if (evento.isEventoAtivo()
                        && evento.getIngressosDisponiveis() > 0) {

                    eventosAtivos.add(evento);
                }
            }
        }

        return eventosAtivos;
    }

    // =====================================================
    // INGRESSOS
    // =====================================================

    private void salvarIngresso(
            Ingresso ingresso
    ) {

        if (ingresso == null) {

            throw new PersistenciaException(
                    "Ingresso inválido."
            );
        }

        if (ingresso.getId() == null) {

            ingresso.setId(
                    contadorIngressos++
            );
        }

        Usuario usuario =
                ingresso.getUsuario();

        Evento evento =
                ingresso.getEvento();

        if (usuario == null
                || evento == null) {

            throw new PersistenciaException(
                    "Ingresso inválido."
            );
        }

        List<Ingresso> lista =
                ingressosPorUsuario.computeIfAbsent(
                        usuario.getId(),
                        k -> new ArrayList<>()
                );

        if (!lista.contains(ingresso)) {

            lista.add(ingresso);

            usuario.addIngresso(ingresso);

            evento.addIngresso(ingresso);
        }
    }

    public Map<String, Object> comprarIngresso(
            Long usuarioId,
            Long eventoId
    ) {

        Usuario usuario =
                buscarUsuarioPorId(usuarioId);

        Evento evento =
                buscarEvento(eventoId);

        if (!evento.isEventoAtivo()) {

            throw new EventoInativoException(
                    "Evento está inativo."
            );
        }

        if (evento.getIngressosDisponiveis() <= 0) {

            throw new EventoSemIngressosDisponiveisException(
                    "Ingressos esgotados."
            );
        }

        if (evento.getDataInicio()
                .isBefore(LocalDateTime.now())) {

            throw new OperacaoRepositorioException(
                    "Evento já iniciado."
            );
        }

        Ingresso ingresso =
                new Ingresso(
                        null,
                        usuario,
                        evento,
                        evento.getPrecoUnitarioIngresso(),
                        usuario.getEmail()
                );

        salvarIngresso(ingresso);

        evento.setIngressosDisponiveis(
                evento.getIngressosDisponiveis() - 1
        );

        Map<String, Object> resultado =
                new HashMap<>();

        resultado.put(
                "ingresso",
                ingresso
        );

        resultado.put(
                "valorTotal",
                ingresso.getValorPago()
        );

        return resultado;
    }

    public boolean cancelarIngresso(
            Long usuarioId,
            Long ingressoId
    ) {

        List<Ingresso> ingressos =
                ingressosPorUsuario.get(usuarioId);

        if (ingressos == null) {

            throw new IngressoNaoEncontradoException(
                    "Ingresso não encontrado."
            );
        }

        for (Ingresso ingresso :
                ingressos) {

            if (ingresso.getId().equals(ingressoId)
                    && !ingresso.isCancelado()) {

                Evento evento =
                        ingresso.getEvento();

                if (!evento.isEventoEstorno()) {

                    throw new CancelamentoNaoPermitidoException(
                            "Evento não permite estorno."
                    );
                }

                double taxa =
                        evento.getTaxaCancelamento();

                double valorEstorno =
                        ingresso.getValorPago()
                                * (1 - taxa / 100.0);

                ingresso.setValorEstornado(
                        valorEstorno
                );

                ingresso.setStatus(
                        Ingresso.StatusIngresso.CANCELADO
                );

                evento.setIngressosDisponiveis(
                        evento.getIngressosDisponiveis() + 1
                );

                return true;
            }
        }

        throw new IngressoNaoEncontradoException(
                "Ingresso não encontrado."
        );
    }

    public List<Ingresso> listarIngressosUsuario(
            Long usuarioId
    ) {

        return ingressosPorUsuario.getOrDefault(
                usuarioId,
                Collections.emptyList()
        );
    }

    // =====================================================
    // MÉTODOS AUXILIARES
    // =====================================================

    public boolean emailExiste(String email) {

        for (Usuario usuario :
                usuariosComum.values()) {

            if (email != null
                    && email.equalsIgnoreCase(
                    usuario.getEmail())) {

                return true;
            }
        }

        for (Organizador organizador :
                organizadores.values()) {

            if (email != null
                    && email.equalsIgnoreCase(
                    organizador.getEmail())) {

                return true;
            }
        }

        return false;
    }

    private Evento encontrarEvento(
            Long eventoId
    ) {

        for (Organizador org :
                organizadores.values()) {

            for (Evento evento :
                    org.getEventos()) {

                if (evento.getId()
                        .equals(eventoId)) {

                    return evento;
                }
            }
        }

        return null;
    }

    private Evento buscarEventoPorOrganizador(
            Long eventoId,
            Long organizadorId
    ) {

        List<Evento> lista =
                eventos.get(organizadorId);

        if (lista == null) {

            throw new OrganizadorNaoEncontradoException(
                    "Organizador não encontrado."
            );
        }

        return lista.stream()

                .filter(e ->
                        e.getId()
                                .equals(eventoId)
                )

                .findFirst()

                .orElseThrow(() ->
                        new EventoNaoEncontradoException(
                                "Evento não encontrado."
                        )
                );
    }

    private void validarDatasEvento(
            Evento evento
    ) {

        if (evento.getDataInicio() != null
                && evento.getDataFim() != null) {

            if (evento.getDataFim()
                    .isBefore(
                            evento.getDataInicio()
                    )) {

                throw new OperacaoRepositorioException(
                        "Data final não pode ser anterior à inicial."
                );
            }

            long minutos =
                    Duration.between(
                            evento.getDataInicio(),
                            evento.getDataFim()
                    ).toMinutes();

            if (minutos < 30) {

                throw new OperacaoRepositorioException(
                        "Evento deve possuir no mínimo 30 minutos."
                );
            }
        }
    }

    public void liberarIngressosEvento(
            Long eventoId,
            Long organizadorId
    ) {

        Evento evento =
                buscarEventoPorOrganizador(
                        eventoId,
                        organizadorId
                );

        evento.disponibilizarIngressos(
                evento.getCapacidadeMaxima()
        );
    }

    public void cancelarTodosIngressosEvento(
            Long eventoId
    ) {

        for (Long usuarioId :
                ingressosPorUsuario.keySet()) {

            List<Ingresso> ingressosUsuario =
                    ingressosPorUsuario.get(usuarioId);

            if (ingressosUsuario != null) {

                ingressosUsuario.stream()

                        .filter(i ->
                                i.getEvento()
                                        .getId()
                                        .equals(eventoId)
                                        && !i.isCancelado()
                        )

                        .forEach(ingresso -> {

                            ingresso.setStatus(
                                    Ingresso.StatusIngresso.CANCELADO
                            );

                            Evento evento =
                                    ingresso.getEvento();

                            double valorEstorno;

                            if (evento.isEventoEstorno()) {

                                double taxa =
                                        evento.getTaxaCancelamento();

                                valorEstorno =
                                        ingresso.getValorPago()
                                                * (1 - taxa / 100.0);

                            } else {

                                valorEstorno = 0.0;
                            }

                            ingresso.setValorEstornado(
                                    valorEstorno
                            );
                        });
            }
        }
    }
}