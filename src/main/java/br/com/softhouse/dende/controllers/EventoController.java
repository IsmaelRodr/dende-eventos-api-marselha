package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {

    private final Repositorio repositorio;

    public  EventoController(){
        this.repositorio = Repositorio.getInstance();
    }

    @GetMapping
    public ResponseEntity<?> feedEvento(){
        List<Evento> eventosFiltrados = repositorio.listarEventoAtivos().stream()
                .filter(e -> e.getDataFim().isAfter(LocalDateTime.now()))
                .sorted(Comparator
                        .comparing(Evento::getDataInicio)
                        .thenComparing(Evento::getNome))
                .toList();

        return ResponseEntity.ok(eventosFiltrados);

    }
}
