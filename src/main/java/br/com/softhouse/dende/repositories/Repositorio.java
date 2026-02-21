package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.time.Duration;
import java.util.*;


public class Repositorio {

    private static final Repositorio instance = new Repositorio();
    private final Map<Long, Usuario> usuariosComum;
    private final Map<Long, Organizador> organizadores;
    private final Map<Long, List<Evento>> eventos;
    private final Map<Long, List<Ingresso>> ingressosPorUsuario;

    private Long contadorUsuarios = 1L;
    private Long contadorOrganizadores = 1L;
    private Long contadorEventos = 1L;
    private Long contadorIngressos = 1L;

    // 2. Construtor privado para o Singleton
    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
        this.ingressosPorUsuario = new HashMap<>();
    }

    // 3. O famigerado metodo getInstance() que estava a dar erro!
    public static Repositorio getInstance() {
        return instance;
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

    public void salvarEvento(Long organizadorId, Evento evento) {
        if (evento.getId() == null || evento.getId() == 0) {
            evento.setId(contadorEventos++);
        }
        eventos.computeIfAbsent(organizadorId, o -> new ArrayList<>())
                .add(evento);
    }

    public void atualizarEvento(long organizadorId , Evento evento, long eventoId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        if (evento.getDataInicio() != null && evento.getDataFim() != null) {
            if (evento.getDataFim().isBefore(evento.getDataInicio())) {
                throw new IllegalArgumentException("Data fim não pode ser anterior à data início.");
            } if (Duration.between(evento.getDataInicio(), evento.getDataFim()).toMinutes() < 30) {
                throw new IllegalArgumentException("Evento deve ter no mínimo 30 minutos.");
            }
        }

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

    }

    public void ativarEvento(long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        eventoExistente.setEventoAtivo(true);
    }

    public void desativarEvento(long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));


        eventoExistente.setEventoAtivo(false);
    }

    public List<Evento> listarEventoPorOrganizador(Long  organizadorId) {

        List<Evento> listaEventos = eventos.getOrDefault(organizadorId, Collections.emptyList());

        return listaEventos;

    }


    public List<Evento> listarEventoAtivos(){
        List<Evento> eventosAtivos = new ArrayList<>();
        for ( List<Evento> eventoGerais: eventos.values()){
            for(Evento evento: eventoGerais){
                if (evento.isEventoAtivo() && evento.getCapacidadeMaxima()>0){
                    eventosAtivos.add(evento);

                }
            }
        }
        return eventosAtivos;
    }

    public Ingresso salvarIngresso(Long usuarioId, Ingresso ingresso) {

        if (ingresso.getId() == null) {
            ingresso.setId(contadorIngressos++);
        }

        ingressosPorUsuario
                .computeIfAbsent(usuarioId, u -> new ArrayList<>())
                .add(ingresso);

        return ingresso;
    }

    public List<Ingresso> listarIngressosUsuario(Long usuarioId) {
        return ingressosPorUsuario.getOrDefault(usuarioId, Collections.emptyList());
    }

    public Ingresso buscarIngressoPorId(Long ingressoId) {

        for (List<Ingresso> lista : ingressosPorUsuario.values()) {
            for (Ingresso i : lista) {
                if (i.getId().equals(ingressoId)) {
                    return i;
                }
            }
        }
        return null;
    }

    public List<Ingresso> buscarIngressosPorEvento(Long eventoId) {

        List<Ingresso> resultado = new ArrayList<>();

        for (List<Ingresso> lista : ingressosPorUsuario.values()) {
            for (Ingresso i : lista) {
                if (i.getEvento().getId() == eventoId) {
                    resultado.add(i);
                }
            }
        }

        return resultado;
    }

}
