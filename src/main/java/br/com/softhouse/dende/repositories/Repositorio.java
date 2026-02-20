package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.util.*;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuariosComum;
    private final Map<String, Organizador> organizadores;
    private final Map<Long, List<Evento>> eventos;


    private static long proximoIdEvento = 1;

    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }


    public void desativarEvento(long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));


        eventoExistente.setEventoAtivo(false);
    }

    public List<Evento> listarEventoOrganizador(Long  organizadorId) {

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

}