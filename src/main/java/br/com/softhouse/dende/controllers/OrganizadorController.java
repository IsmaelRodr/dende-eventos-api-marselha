package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
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

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<String> cadastrarEvento(@PathVariable(parameter = "organizadorId") long id, @RequestBody Evento evento){
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = evento.getDataInicio();

        Duration duracao = Duration.ofMinutes(30);

        if(hoje.isAfter(dataInicio)){
            return ResponseEntity.status(422,"A Data de inicio do Evento não é válida!");
        }

        if(hoje.isAfter(evento.getDataFim()) && dataInicio.isAfter(evento.getDataFim())){
            return ResponseEntity.status(422, "A um conflito entre a data de inicio e a data de fim do evento");
        }

        if(!(evento.getDuracaoEvento().compareTo(duracao) > 0)){
            return ResponseEntity.status(422,"Os eventos devem ter no mínimo 30 minutos de duração!");
        }

        repositorio.salvarEvento(evento);

        return ResponseEntity.status(200, "Evento criado com sucesso!");
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(@PathVariable(parameter = "organizadorId") long organizadorId , @PathVariable(parameter = "eventoId") long eventoId, @RequestBody Evento evento){
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = evento.getDataInicio();

        Duration duracao = Duration.ofMinutes(30);

        if(hoje.isAfter(dataInicio)){
            return ResponseEntity.status(422,"A Data de inicio do Evento não é válida!");
        }

        if(hoje.isAfter(evento.getDataFim()) && dataInicio.isAfter(evento.getDataFim())){
            return ResponseEntity.status(422, "A um conflito entre a data de inicio e a data de fim do evento");
        }

        if(!(evento.getDuracaoEvento().compareTo(duracao) > 0)){
            return ResponseEntity.status(422,"Os eventos devem ter no mínimo 30 minutos de duração!");
        }

        if(repositorio.listarEventoOrganizador(evento.getOrganizador())){
            return ResponseEntity.status(409,"O Evento já existe!");
        }

        if(!evento.isEventoAtivo()){
            return ResponseEntity.status(422,"O Evento não está ativo!");
        }

        repositorio.atualizarEvento(evento, eventoId);

        return ResponseEntity.status(200,"Evento Atualizado com sucesso!");
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{status}")
    public ResponseEntity<String> ativarEvento(@PathVariable(parameter = "organizadorId") long organizadorId, @PathVariable(parameter = "status") String status, @RequestBody Evento evento){

        if(!(repositorio.listarEventoOrganizador(evento.getOrganizador()))){
            return ResponseEntity.status(409,"O Evento não existe!");
        }

        if(evento.isEventoAtivo()){
            return ResponseEntity.status(422,"O Evento já está ativo!");
        }

        if(status.equals("ativar")){
            repositorio.ativarEvento(evento, evento.getId());
            return ResponseEntity.status(202,"Evento Ativado!");
        }

        return ResponseEntity.status(404, "O " + status + " não foi encontrado!");

    }

    @PatchMapping(path = "/{organizadorId}/eventos/{status}")
    public ResponseEntity<String> desativarEvento(@PathVariable(parameter = "organizadorId") long organizadorId, @PathVariable(parameter = "status") String status, @RequestBody Evento evento){

        if(!(repositorio.listarEventoOrganizador(evento.getOrganizador()))){
            return ResponseEntity.status(409,"O Evento não existe!");
        }

        if(!evento.isEventoAtivo()){
            return ResponseEntity.status(422,"O Evento já está desativado!");
        }

        if(status.equals("desativar")){
            repositorio.desativarEvento(evento, evento.getId());
            return ResponseEntity.status(202,"Evento desativado!");
        }

        return ResponseEntity.status(404, "O " + status + " não foi encontrado!");

    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEventos(@PathVariable(parameter = "organizadorId")long organizadorId){

        List<Evento> listadeEventos = repositorio.listarEventoOrganizador(organizadorId);

        if (listadeEventos.isEmpty()){

            return ResponseEntity.status(404,"nao ha Eventos");
        }

       return ResponseEntity.ok(listadeEventos);

    }
}
