package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuariosComum;
    private final Map<String, Organizador> organizadores;
    private final Map<Organizador, List<Evento>> eventos;


    private static long proximoIdEvento = 1;

    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    public void salvarEvento(Evento evento) {
        evento.setId(proximoIdEvento++);
        eventos.computeIfAbsent(evento.getOrganizador(), o -> new ArrayList<>())
                .add(evento);
    }

    public Evento atualizarEvento(Evento evento, long id){
        List<Evento> lista = eventos.get(evento.getOrganizador());

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == id).findFirst().orElseThrow();

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
        eventoExistente.setDuracaoEvento(evento.getDuracaoEvento());

        return eventoExistente;
    }

    public void ativarEvento(Evento evento, long id){
        List<Evento> lista = eventos.get(evento.getOrganizador());

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == id).findFirst().orElseThrow();

        eventoExistente.setEventoAtivo(evento.isEventoAtivo());
    }

}
