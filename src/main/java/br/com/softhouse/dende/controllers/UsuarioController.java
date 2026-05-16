package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;

import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;

import br.com.dende.softhouse.process.route.ResponseEntity;

import br.com.softhouse.dende.dto.LoginDto;
import br.com.softhouse.dende.dto.usuario.AtualizarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.CadastrarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.StatusUsuarioDto;
import br.com.softhouse.dende.dto.usuario.VisualizarUsuarioDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.usuario.CredenciaisInvalidasException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioJaAtivoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioNaoEncontradoException;

import br.com.softhouse.dende.services.UsuarioService;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    @PostMapping()
    public ResponseEntity<?> cadastroUsuario(
            @RequestBody CadastrarUsuarioDto dto
    ) {
        try {
            StatusUsuarioDto resposta = usuarioService.cadastrar(dto);
            return ResponseEntity.status(201, resposta);
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable(parameter = "id") String id,
            @RequestBody AtualizarUsuarioDto dto
    ) {
        try {
            Long idNumerico = Long.parseLong(id);
            StatusUsuarioDto resposta = usuarioService.atualizar(idNumerico, dto);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @GetMapping(path = "/{usuarioId}")
    public ResponseEntity<?> visualizarPerfil(
            @PathVariable(parameter = "usuarioId") String usuarioId
    ) {
        try {
            Long id = Long.parseLong(usuarioId);
            VisualizarUsuarioDto response = usuarioService.buscarPorId(id);
            return ResponseEntity.status(200, response);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PatchMapping(path = "/{usuarioId}/desativar")
    public ResponseEntity<?> desativarUsuario(
            @PathVariable(parameter = "usuarioId") String usuarioId
    ) {
        try {
            Long id = Long.parseLong(usuarioId);
            StatusUsuarioDto response = usuarioService.desativar(id);
            return ResponseEntity.status(200, response);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PatchMapping(path = "/{usuarioId}/ativar")
    public ResponseEntity<?> ativarUsuario(
            @PathVariable(parameter = "usuarioId") String usuarioId,
            @RequestBody LoginDto dto
    ) {
        try {
            Long id = Long.parseLong(usuarioId);
            StatusUsuarioDto response = usuarioService.ativar(id, dto);
            return ResponseEntity.status(200, response);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (UsuarioNaoEncontradoException | CredenciaisInvalidasException e) {
            return ResponseEntity.status(401, e.getMessage());
        } catch (UsuarioJaAtivoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }
}