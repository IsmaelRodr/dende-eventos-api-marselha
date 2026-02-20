package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.Duration;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController(){
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<String> cadastrarEvento(@PathVariable(parameter = "organizadorId") long organizadorId, @RequestBody Evento evento){
        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataInicio = evento.getDataInicio();
        LocalDateTime dataFim = evento.getDataFim();

        long duracao = Duration.between(dataInicio,dataFim).toMinutes();

        if(dataInicio.isBefore(hoje)){
            return ResponseEntity.status(422,"A Data de inicio do Evento não é válida!");
        }

        if(dataFim.isBefore(hoje) && dataInicio.isAfter(dataFim)){
            return ResponseEntity.status(422, "A um conflito entre a data de inicio e a data de fim do evento");
        }

        if(duracao < 30){
            return ResponseEntity.status(422,"Os eventos devem ter no mínimo 30 minutos de duração!");
        }

        repositorio.salvarEvento(organizadorId, evento);

        return ResponseEntity.status(200, "Evento criado com sucesso!");
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(@PathVariable(parameter = "organizadorId") long organizadorId , @PathVariable(parameter = "eventoId") long eventoId, @RequestBody Evento evento){
        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataInicio = evento.getDataInicio();
        LocalDateTime dataFim = evento.getDataFim();

        long duracao = Duration.between(dataInicio,dataFim).toMinutes();

        if(dataInicio.isBefore(hoje)){
            return ResponseEntity.status(422,"A Data de inicio do Evento não é válida!");
        }

        if(dataFim.isBefore(hoje) && dataInicio.isAfter(dataFim)){
            return ResponseEntity.status(422, "A um conflito entre a data de inicio e a data de fim do evento");
        }

        if(duracao < 30){
            return ResponseEntity.status(422,"Os eventos devem ter no mínimo 30 minutos de duração!");
        }

        Evento eventoExistente = repositorio.listarEventoOrganizador(organizadorId)
                .stream()
                .filter(e -> e.getId() == eventoId)
                .findFirst()
                .orElse(null);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento não existe!");
        }

        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está ativo!");
        }

        repositorio.atualizarEvento(organizadorId, evento, eventoId);

        return ResponseEntity.status(200,"Evento Atualizado com sucesso!");
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/{status}")
    public ResponseEntity<String> ativarEvento(@PathVariable(parameter = "organizadorId") long organizadorId, @PathVariable(parameter = "eventoId") long eventoId, @PathVariable(parameter = "status") String status, @RequestBody Evento evento){

        Evento eventoExistente = repositorio.listarEventoOrganizador(organizadorId)
                .stream()
                .filter(e -> e.getId() == eventoId)
                .findFirst()
                .orElse(null);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento não existe!");
        }

        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está ativo!");
        }


        if(status.equals("ativar")){
            repositorio.ativarEvento(evento, eventoId, organizadorId);
            return ResponseEntity.status(202,"Evento Ativado!");
        }

        return ResponseEntity.status(404, "O " + status + " não foi encontrado!");

    }

}
