package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;

import br.com.softhouse.dende.dto.ingresso.IngressoGeradoDto;
import br.com.softhouse.dende.dto.ingresso.ResultadoCompraIngressoDto;
import br.com.softhouse.dende.dto.usuario.CancelarIngressoUsuarioDto;
import br.com.softhouse.dende.dto.usuario.ListaIngressosUsuarioDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.evento.EventoNaoEncontradoException;

import br.com.softhouse.dende.exceptions.ingresso.CancelamentoNaoPermitidoException;
import br.com.softhouse.dende.exceptions.ingresso.CompraIngressoException;
import br.com.softhouse.dende.exceptions.ingresso.IngressoNaoEncontradoException;

import br.com.softhouse.dende.exceptions.usuario.UsuarioNaoEncontradoException;

import br.com.softhouse.dende.mapper.IngressoMapper;
import br.com.softhouse.dende.mapper.UsuarioMapper;

import br.com.softhouse.dende.model.Ingresso;

import br.com.softhouse.dende.services.IngressoService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/ingressos")
public class IngressoController {

    private final IngressoService ingressoService;

    public IngressoController() {
        this.ingressoService = new IngressoService();
    }

    @PostMapping(
            path = "/organizadores/{organizadorId}/eventos/{eventoId}"
    )
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "organizadorId") String organizadorIdString,
            @PathVariable(parameter = "eventoId") String eventoIdString,
            @RequestBody Map<String, Long> request
    ) {
        try {
            Long organizadorId = Long.parseLong(organizadorIdString);
            Long eventoId = Long.parseLong(eventoIdString);
            if (request == null || request.get("usuarioId") == null) {
                throw new DadosInvalidosException(
                        "usuarioId é obrigatório."
                );
            }
            Long usuarioId = request.get("usuarioId");
            ResultadoCompraIngressoDto resposta = ingressoService.comprarIngresso(
                    usuarioId,
                    eventoId,
                    organizadorId
            );
            return ResponseEntity.status(201, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (UsuarioNaoEncontradoException | EventoNaoEncontradoException | IngressoNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (CompraIngressoException | CancelamentoNaoPermitidoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno do servidor.");
        }
    }

    @PostMapping(
            path = "/usuarios/{usuarioId}/{ingressoId}/cancelar"
    )
    public ResponseEntity<?> cancelarIngresso(
            @PathVariable(parameter = "usuarioId") String usuarioIdString,
            @PathVariable(parameter = "ingressoId") String ingressoIdString
    ) {
        try {
            Long usuarioId = Long.parseLong(usuarioIdString);
            Long ingressoId = Long.parseLong(ingressoIdString);
            CancelarIngressoUsuarioDto resposta = ingressoService.cancelarIngresso( usuarioId, ingressoId);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (UsuarioNaoEncontradoException | EventoNaoEncontradoException | IngressoNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (CancelamentoNaoPermitidoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno do servidor.");
        }
    }

    @GetMapping(
            path = "/usuarios/{usuarioId}"
    )
    public ResponseEntity<?> listarIngressos(
            @PathVariable(parameter = "usuarioId") String usuarioIdString
    ) {
        try {
            Long usuarioId = Long.parseLong(usuarioIdString);
            List<ListaIngressosUsuarioDto> lista = ingressoService.listarIngressosUsuario(usuarioId);
            return ResponseEntity.status(200, lista);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno do servidor."
            );
        }
    }
}