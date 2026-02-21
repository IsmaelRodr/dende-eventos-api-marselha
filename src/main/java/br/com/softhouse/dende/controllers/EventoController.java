package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {

    private final Repositorio repositorio;

    public  EventoController(){
        this.repositorio = Repositorio.getInstance();
    }

    @GetMapping
    public ResponseEntity<?> feedEvento(){
        List<Map<String, Object>> eventosFiltrados = repositorio.listarEventoAtivos().stream()
                .filter(e -> e.getDataFim().isAfter(LocalDateTime.now()))
                .sorted(Comparator
                        .comparing(Evento::getDataInicio)
                        .thenComparing(Evento::getNome))
                .map(e -> { Map<String, Object> eventoMap = new HashMap<>();
                    eventoMap.put("nome", e.getNome());
                    eventoMap.put("dataInicio", e.getDataInicio());
                    eventoMap.put("dataFim", e.getDataFim());
                    eventoMap.put("local", e.getLocalEvento());
                    eventoMap.put("precoIngresso", e.getPrecoUnitarioIngresso());
                    eventoMap.put("capacidadeMaxima", e.getCapacidadeMaxima());
                    return eventoMap; })
                .toList();

        return ResponseEntity.ok(eventosFiltrados);

    }
}
