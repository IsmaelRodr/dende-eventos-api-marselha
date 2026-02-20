package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController(){
        this.repositorio = Repositorio.getInstance();
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/{status}")
    public ResponseEntity<String> desativarEvento(@PathVariable(parameter = "organizadorId") long organizadorId, @PathVariable(parameter = "eventoId") long eventoId, @PathVariable(parameter = "status") String status){

        Evento eventoExistente = repositorio.listarEventoOrganizador(organizadorId)
                .stream()
                .filter(e -> e.getId() == eventoId)
                .findFirst()
                .orElse(null);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento não existe!");
        }

        if (!eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está desativado!");
        }


        if ("desativar".equalsIgnoreCase(status)){
            repositorio.desativarEvento(eventoId, organizadorId);
            return ResponseEntity.status(200,"Evento desativado!");
        }

        return ResponseEntity.status(404, "O " + status + " não foi encontrado!");

    }



    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEvento (@PathVariable(parameter = "organizadorId")long organizadorId){

        List<Evento> listadeEventos = repositorio.listarEventoOrganizador(organizadorId);

        if (listadeEventos.isEmpty()){

            return ResponseEntity.status(200,"nao ha Eventos");
        }

       return ResponseEntity.ok(listadeEventos);

    }
}
