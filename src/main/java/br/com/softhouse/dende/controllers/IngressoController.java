package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.repositories.Repositorio;
import br.com.softhouse.dende.service.IngressoService;

import java.util.List;

@Controller
@RequestMapping(path = "")
public class IngressoController {

    private final Repositorio repositorio;
    private final IngressoService ingressoService;

    public IngressoController() {
        this.repositorio = Repositorio.getInstance();
        this.ingressoService = new IngressoService();
    }

    @PostMapping(path = "/organizadores/{organizadorId}/eventos/{eventoId}/ingressos")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "eventoId") String eventoId,
            @RequestBody Usuario usuario
    ) {

        Evento evento = buscarEvento(eventoId);
        if (evento == null)
            return ResponseEntity.status(404, "Evento não encontrado.");

        try {
            List<Ingresso> ingressos =
                    ingressoService.comprarIngresso(usuario, evento);

            return ResponseEntity.status(201, ingressos);

        } catch (Exception e) {
            return ResponseEntity.status(422, e.getMessage());
        }
    }

    @PostMapping(path = "/usuarios/{usuarioId}/ingressos/{ingressoId}")
    public ResponseEntity<?> cancelarIngresso(
            @PathVariable(parameter = "ingressoId") String ingressoId
    ) {

        Ingresso ingresso =
                repositorio.buscarIngressoPorId(Long.parseLong(ingressoId));

        if (ingresso == null)
            return ResponseEntity.status(404, "Ingresso não encontrado.");

        try {
            ingressoService.cancelarIngresso(ingresso);
            return ResponseEntity.ok("Ingresso cancelado.");

        } catch (Exception e) {
            return ResponseEntity.status(422, e.getMessage());
        }
    }

    @GetMapping(path = "/usuarios/{usuarioId}/ingressos")
    public ResponseEntity<?> listarIngressos(
            @PathVariable(parameter = "usuarioId") String usuarioId
    ) {

        List<Ingresso> ingressos =
                ingressoService.listarIngressosUsuario(Long.parseLong(usuarioId));

        return ResponseEntity.ok(ingressos);
    }

    private Evento buscarEvento(String eventoId) {

        long id = Long.parseLong(eventoId);

        return repositorio.listarEventoAtivos()
                .stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }
}