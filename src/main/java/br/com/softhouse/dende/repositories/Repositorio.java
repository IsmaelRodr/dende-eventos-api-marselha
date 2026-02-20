package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.Organizador;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuariosComum;
    private final Map<String, Organizador> organizadores;
    private final Map<Long, List<Evento>> eventos;

    // 1. Padrão Singleton (A arquitetura que os seus colegas usaram)
    private static final Repositorio instance = new Repositorio();

    // As listas guardam os utilizadores usando o ID numérico (Long)
    private final Map<Long, Usuario> usuariosComum;
    private final Map<Long, Organizador> organizadores;

    private Long contadorUsuarios = 1L;
    private Long contadorOrganizadores = 1L;

    // 2. Construtor privado para o Singleton
    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
    }

    // 3. O famigerado metodo getInstance() que estava a dar erro!
    public static Repositorio getInstance() {
        return instance;
    }

    public void salvarEvento(Long organizadorId, Evento evento) {
        eventos.computeIfAbsent(organizadorId, o -> new ArrayList<>())
                .add(evento);
    }

    public Evento atualizarEvento(long organizadorId ,Evento evento, long eventoId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        eventoExistente.setNome(evento.getNome());
        eventoExistente.setDescricao(evento.getDescricao());
        eventoExistente.setPaginaWeb(evento.getPaginaWeb());
        eventoExistente.setDataInicio(evento.getDataInicio());
        eventoExistente.setDataFim(evento.getDataFim());
        eventoExistente.setTipoEvento(evento.getTipoEvento());
        eventoExistente.setEventoPrincipal(evento.getEventoPrincipal());
        eventoExistente.setModalidade(evento.getModalidade());
        eventoExistente.setPrecoUnitarioIngresso(evento.getPrecoUnitarioIngresso());
        eventoExistente.setTaxaCancelamento(evento.getTaxaCancelamento());
        eventoExistente.setEventoEstorno(evento.isEventoEstorno());
        eventoExistente.setCapacidadeMaxima(evento.getCapacidadeMaxima());
        eventoExistente.setLocalEvento(evento.getLocalEvento());

        return eventoExistente;
    }

    public void ativarEvento(Evento evento, long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        eventoExistente.setEventoAtivo(true);
    }

}
    public void salvarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(contadorUsuarios++);
        }
        usuariosComum.put(usuario.getId(), usuario);
    }

    public void salvarOrganizador(Organizador organizador) {
        if (organizador.getId() == null) {
            organizador.setId(contadorOrganizadores++);
        }
        organizadores.put(organizador.getId(), organizador);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuariosComum.get(id);
    }

    public Organizador buscarOrganizadorPorId(Long id) {
        return organizadores.get(id);
    }

    public boolean emailExiste(String email) {
        for (Usuario u : usuariosComum.values()) {
            if (email != null && email.equals(u.getEmail())) return true;
        }
        for (Organizador o : organizadores.values()) {
            if (email != null && email.equals(o.getEmail())) return true;
        }
        return false;
    }

    // --- MÉTODOS DE ATUALIZAR COMPLETOS (Pedido do Líder) ---

    public void atualizarDadosUsuario(Usuario usuarioExistente, Usuario novosDados) {
        if (novosDados.getNome() != null)
            usuarioExistente.setNome(novosDados.getNome());

        if (novosDados.getDataNascimento() != null)
            usuarioExistente.setDataNascimento(novosDados.getDataNascimento());

        if (novosDados.getSexo() != null)
            usuarioExistente.setSexo(novosDados.getSexo());

        if (novosDados.getSenha() != null)
            usuarioExistente.setSenha(novosDados.getSenha());

        usuariosComum.put(usuarioExistente.getId(), usuarioExistente);
    }

    public void atualizarDadosOrganizador(Organizador orgExistente, Organizador novosDados) {
        if (novosDados.getNome() != null)
            orgExistente.setNome(novosDados.getNome());

        if (novosDados.getDataNascimento() != null)
            orgExistente.setDataNascimento(novosDados.getDataNascimento());

        if (novosDados.getSexo() != null)
            orgExistente.setSexo(novosDados.getSexo());

        if (novosDados.getSenha() != null)
            orgExistente.setSenha(novosDados.getSenha());

        orgExistente.setEmpresa(novosDados.getEmpresa());  // Nova classe Empresa

        organizadores.put(orgExistente.getId(), orgExistente);
    }

}
