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

import br.com.softhouse.dende.dto.usuario.AtualizarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.CadastrarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.StatusUsuarioDto;
import br.com.softhouse.dende.dto.usuario.VisualizarUsuarioDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.ConflictException;
import br.com.softhouse.dende.exceptions.UsuarioNaoEncontradoException;

import br.com.softhouse.dende.mapper.UsuarioMapper;

import br.com.softhouse.dende.model.Usuario;

import br.com.softhouse.dende.services.UsuarioService;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController() {

        this.usuarioService =
                new UsuarioService();
    }

    @PostMapping(path = "")
    public ResponseEntity cadastroUsuario(

            @RequestBody
            CadastrarUsuarioDto dto
    ) {

        try {

            Usuario usuario =
                    usuarioService.cadastrar(dto);

            StatusUsuarioDto resposta =
                    UsuarioMapper.toStatusDto(
                            "Usuário registrado com sucesso!",
                            usuario
                    );

            return ResponseEntity.status(
                    201,
                    resposta
            );

        } catch (DadosInvalidosException e) {

            return ResponseEntity.status(
                    400,
                    e.getMessage()
            );

        } catch (ConflictException e) {

            return ResponseEntity.status(
                    409,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno no servidor."
            );
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity atualizarUsuario(

            @PathVariable(parameter = "id")
            String id,

            @RequestBody
            AtualizarUsuarioDto dto
    ) {

        try {

            Long idNumerico =
                    Long.parseLong(id);

            Usuario usuario =
                    usuarioService.atualizar(
                            idNumerico,
                            dto
                    );

            StatusUsuarioDto resposta =
                    UsuarioMapper.toStatusDto(
                            "Perfil do usuário atualizado com sucesso!",
                            usuario
                    );

            return ResponseEntity.status(
                    200,
                    resposta
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (UsuarioNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (DadosInvalidosException e) {

            return ResponseEntity.status(
                    400,
                    e.getMessage()
            );

        } catch (ConflictException e) {

            return ResponseEntity.status(
                    409,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno no servidor."
            );
        }
    }

    @GetMapping(path = "/{usuarioId}")
    public ResponseEntity visualizarPerfil(

            @PathVariable(parameter = "usuarioId")
            String usuarioId
    ) {

        try {

            Long id =
                    Long.parseLong(usuarioId);

            Usuario usuario =
                    usuarioService.buscarPorId(id);

            VisualizarUsuarioDto response =
                    UsuarioMapper.toVisualizarDto(
                            usuario
                    );

            return ResponseEntity.status(
                    200,
                    response
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (UsuarioNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno no servidor."
            );
        }
    }

    @PatchMapping(path = "/{usuarioId}/desativar")
    public ResponseEntity desativarUsuario(

            @PathVariable(parameter = "usuarioId")
            String usuarioId
    ) {

        try {

            Long id =
                    Long.parseLong(usuarioId);

            usuarioService.desativar(id);

            return ResponseEntity.status(
                    200,
                    "Usuário desativado com sucesso!"
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (UsuarioNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (ConflictException e) {

            return ResponseEntity.status(
                    409,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno no servidor."
            );
        }
    }

    @PatchMapping(path = "/{usuarioId}/ativar")
    public ResponseEntity ativarUsuario(

            @PathVariable(parameter = "usuarioId")
            String usuarioId
    ) {

        try {

            Long id =
                    Long.parseLong(usuarioId);

            usuarioService.ativar(id);

            return ResponseEntity.status(
                    200,
                    "Usuário ativado com sucesso!"
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        }
    }
}