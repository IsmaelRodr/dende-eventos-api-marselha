package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;
import br.com.softhouse.dende.dto.usuario.ListaIngressosUsuarioDto;
import br.com.softhouse.dende.mapper.IngressoMapper;

import java.util.List;

@Controller
@RequestMapping(path = "/usuarios")
public class IngressoController {

    private final Repositorio repositorio;

    public IngressoController() {
        this.repositorio = Repositorio.getInstance();
    }

    // Rota que estava no UsuarioController
    @PostMapping(path = "/{usuarioId}/ingressos/{ingressoId}")
    public ResponseEntity<String> cancelarIngresso(
            @PathVariable(parameter = "usuarioId") String usuarioIdString,
            @PathVariable(parameter = "ingressoId") String ingressoIdString) {

        try {
            long usuarioId = Long.parseLong(usuarioIdString);
            long ingressoId = Long.parseLong(ingressoIdString);

            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            if (usuario == null || !usuario.isAtivo()) {
                return ResponseEntity.status(404, "Usuário não encontrado");
            }

            boolean cancelado = repositorio.cancelarIngresso(usuarioId, ingressoId);
            if (!cancelado) {
                return ResponseEntity.status(404, "Ingresso não encontrado ou já cancelado");
            }
            return ResponseEntity.status(200, "Ingresso cancelado com sucesso e o valor foi estornado.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422, e.getMessage()); // 422 Unprocessable Entity
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }

    // Rota que estava no UsuarioController - US 15
    @GetMapping(path = "/{usuarioId}/ingressos")
    public ResponseEntity<?> listarIngressos(@PathVariable(parameter = "usuarioId") String usuarioIdString) {
        try {
            long usuarioId = Long.parseLong(usuarioIdString);
            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            if (usuario == null) return ResponseEntity.status(404, "Usuário não encontrado");

            List<Ingresso> ingressos = repositorio.listarIngressosUsuario(usuarioId);
            
            // ALTERADO: Retorna a lista de DTOs mapeados através do IngressoMapper em vez do HashMap
            List<ListaIngressosUsuarioDto> lista = ingressos.stream()
                    .map(IngressoMapper::toListaUsuarioDto)
                    .toList();

            return ResponseEntity.status(200, lista);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }
}