package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;

import br.com.softhouse.dende.dto.evento.FeedEventoDto;

import br.com.softhouse.dende.exceptions.evento.EventoInativoException;
import br.com.softhouse.dende.exceptions.evento.EventoNaoEncontradoException;

import br.com.softhouse.dende.exceptions.repository.OperacaoRepositorioException;

import br.com.softhouse.dende.mapper.EventoMapper;

import br.com.softhouse.dende.model.Evento;

import br.com.softhouse.dende.services.EventoService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController() {

        this.eventoService =
                new EventoService();
    }

    @GetMapping
    public ResponseEntity<?> feedEvento() {

        try {

            List<FeedEventoDto> eventosFiltrados =

                    eventoService
                            .listarEventosAtivos()

                            .stream()

                            .filter(evento ->
                                    evento.getDataFim()
                                            .isAfter(
                                                    LocalDateTime.now()
                                            )
                            )

                            .sorted(
                                    Comparator

                                            .comparing(
                                                    Evento::getDataInicio
                                            )

                                            .thenComparing(
                                                    Evento::getNome
                                            )
                            )

                            .map(
                                    EventoMapper::toFeedEventoDto
                            )

                            .toList();

            return ResponseEntity.status(
                    200,
                    eventosFiltrados
            );

        } catch (EventoNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (EventoInativoException e) {

            return ResponseEntity.status(
                    422,
                    e.getMessage()
            );

        } catch (OperacaoRepositorioException e) {

            return ResponseEntity.status(
                    400,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno do servidor."
            );
        }
    }
}