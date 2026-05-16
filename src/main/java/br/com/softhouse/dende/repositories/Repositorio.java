package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.util.*;

public class Repositorio {

    private static final Repositorio instance = new Repositorio();

    private final Map<Long, Usuario> usuariosComum = new HashMap<>();
    private final Map<Long, Organizador> organizadores = new HashMap<>();
    private final Map<Long, List<Evento>> eventosPorOrganizador = new HashMap<>();
    private final Map<Long, List<Ingresso>> ingressosPorUsuario = new HashMap<>();

    private Long contadorUsuarios = 1L;
    private Long contadorOrganizadores = 1L;
    private Long contadorEventos = 1L;
    private Long contadorIngressos = 1L;

    private Repositorio() {}

    public static Repositorio getInstance() {
        return instance;
    }

    //USUÁRIOS
    public void salvarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(contadorUsuarios++);
        }
        usuariosComum.put(usuario.getId(), usuario);
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return Optional.ofNullable(usuariosComum.get(id));
    }

    // ===================== ORGANIZADORES =====================
    public void salvarOrganizador(Organizador organizador) {
        if (organizador.getId() == null) {
            organizador.setId(contadorOrganizadores++);
        }
        organizadores.put(organizador.getId(), organizador);
    }

    public Optional<Organizador> buscarOrganizadorPorId(Long id) {
        return Optional.ofNullable(organizadores.get(id));
    }

    // ===================== EVENTOS =====================
    public Evento salvarEvento(Organizador organizador, Evento evento) {
        if (evento.getId() == null) {
            evento.setId(contadorEventos++);
        }
        eventosPorOrganizador
                .computeIfAbsent(organizador.getId(), k -> new ArrayList<>())
                .add(evento);
        return evento;
    }

    public Evento atualizarEvento(Evento evento) {
        // Assume que o evento já existe; substitui na lista do organizador
        // Isso será chamado pelo service, que garante a integridade
        return evento; // a referência já está na lista
    }

    public Optional<Evento> buscarEventoPorId(Long eventoId) {
        return eventosPorOrganizador.values().stream()
                .flatMap(List::stream)
                .filter(e -> e.getId().equals(eventoId))
                .findFirst();
    }

    public List<Evento> listarEventosPorOrganizador(Organizador organizador) {
        return organizador.getEventos();
    }

    public List<Evento> listarEventosAtivos() {
        List<Evento> ativos = new ArrayList<>();
        for (List<Evento> lista : eventosPorOrganizador.values()) {
            for (Evento e : lista) {
                if (e.isEventoAtivo() && e.getIngressosDisponiveis() > 0) {
                    ativos.add(e);
                }
            }
        }
        return ativos;
    }

    // ===================== INGRESSOS =====================
    public Ingresso salvarIngresso(Ingresso ingresso) {
        if (ingresso.getId() == null) {
            ingresso.setId(contadorIngressos++);
        }
        Long usuarioId = ingresso.getUsuario().getId();
        ingressosPorUsuario
                .computeIfAbsent(usuarioId, k -> new ArrayList<>())
                .add(ingresso);
        return ingresso;
    }

    public List<Ingresso> listarIngressosPorUsuario(Long usuarioId) {
        return ingressosPorUsuario.getOrDefault(usuarioId, Collections.emptyList());
    }

    public Optional<Ingresso> buscarIngressoPorId(Long ingressoId) {
        return ingressosPorUsuario.values().stream()
                .flatMap(List::stream)
                .filter(i -> i.getId().equals(ingressoId))
                .findFirst();
    }

    // ===================== UTILITÁRIOS =====================
    public boolean emailExiste(String email) {
        return usuariosComum.values().stream().anyMatch(u -> email.equalsIgnoreCase(u.getEmail())) ||
                organizadores.values().stream().anyMatch(o -> email.equalsIgnoreCase(o.getEmail()));
    }

    public boolean isEventoDoOrganizador(Long organizadorId, Long eventoId) {
        List<Evento> eventos = eventosPorOrganizador.get(organizadorId);
        if (eventos == null) return false;
        return eventos.stream().anyMatch(e -> e.getId().equals(eventoId));
    }
}