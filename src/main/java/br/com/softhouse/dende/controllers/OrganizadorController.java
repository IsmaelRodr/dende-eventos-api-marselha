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

import br.com.softhouse.dende.dto.evento.EventosOrganizadorDto;

import br.com.softhouse.dende.dto.organizador.AtualizarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.CadastrarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.StatusOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.VisualizarOrganizadorDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.organizador.OrganizadorNaoEncontradoException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorJaAtivoException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorJaInativoException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorComEventosAtivosException;

import br.com.softhouse.dende.exceptions.usuario.EmailJaCadastradoException;

import br.com.softhouse.dende.mapper.EventoMapper;
import br.com.softhouse.dende.mapper.OrganizadorMapper;

import br.com.softhouse.dende.model.Organizador;

import br.com.softhouse.dende.services.EventoService;
import br.com.softhouse.dende.services.OrganizadorService;

import java.util.List;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final OrganizadorService organizadorService;

    private final EventoService eventoService;

    public OrganizadorController() {

        this.organizadorService =
                new OrganizadorService();

        this.eventoService =
                new EventoService();
    }

    @PostMapping
    public ResponseEntity<?> cadastroOrganizador(

            @RequestBody
            CadastrarOrganizadorDto dto
    ) {

        try {

            Organizador organizador =
                    organizadorService.cadastrar(dto);

            StatusOrganizadorDto response =
                    OrganizadorMapper.toStatusDto(
                            "Organizador registrado com sucesso!",
                            organizador
                    );

            return ResponseEntity.status(
                    201,
                    response
            );

        } catch (DadosInvalidosException e) {

            return ResponseEntity.status(
                    400,
                    e.getMessage()
            );

        } catch (EmailJaCadastradoException e) {

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

    @PutMapping(path = "/{organizadorId}")
    public ResponseEntity<?> atualizarOrganizador(

            @PathVariable(parameter = "organizadorId")
            String organizadorId,

            @RequestBody
            AtualizarOrganizadorDto dto
    ) {

        try {

            Long id =
                    Long.parseLong(organizadorId);

            Organizador organizador =
                    organizadorService.atualizar(
                            id,
                            dto
                    );

            StatusOrganizadorDto response =
                    OrganizadorMapper.toStatusDto(
                            "Organizador atualizado com sucesso!",
                            organizador
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

        } catch (DadosInvalidosException e) {

            return ResponseEntity.status(
                    400,
                    e.getMessage()
            );

        } catch (OrganizadorNaoEncontradoException e) {

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

    @GetMapping(path = "/{organizadorId}")
    public ResponseEntity<?> visualizarPerfilOrganizador(

            @PathVariable(parameter = "organizadorId")
            String organizadorId
    ) {

        try {

            Long id =
                    Long.parseLong(organizadorId);

            Organizador organizador =
                    organizadorService.buscarPorId(id);

            VisualizarOrganizadorDto dto =
                    OrganizadorMapper.toVisualizarDto(
                            organizador
                    );

            return ResponseEntity.status(
                    200,
                    dto
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (OrganizadorNaoEncontradoException e) {

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

    @PatchMapping(path = "/{organizadorId}/ativar")
    public ResponseEntity<?> ativarOrganizador(

            @PathVariable(parameter = "organizadorId")
            String organizadorId
    ) {

        try {

            Long id =
                    Long.parseLong(organizadorId);

            organizadorService.ativar(id);

            return ResponseEntity.status(
                    200,
                    "Organizador ativado com sucesso!"
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (OrganizadorNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (OrganizadorJaAtivoException e) {

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

    @PatchMapping(path = "/{organizadorId}/desativar")
    public ResponseEntity<?> desativarOrganizador(

            @PathVariable(parameter = "organizadorId")
            String organizadorId
    ) {

        try {

            Long id =
                    Long.parseLong(organizadorId);

            organizadorService.desativar(id);

            return ResponseEntity.status(
                    200,
                    "Organizador desativado com sucesso!"
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (OrganizadorNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (OrganizadorJaInativoException e) {

            return ResponseEntity.status(
                    409,
                    e.getMessage()
            );

        } catch (OrganizadorComEventosAtivosException e) {

            return ResponseEntity.status(
                    422,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno no servidor."
            );
        }
    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEventosDoOrganizador(

            @PathVariable(parameter = "organizadorId")
            String organizadorId
    ) {

        try {

            Long id =
                    Long.parseLong(organizadorId);

            List<EventosOrganizadorDto> eventos =
                    eventoService
                            .listarEventosOrganizador(id)
                            .stream()
                            .map(EventoMapper::toEventosOrganizadorDto)
                            .toList();

            return ResponseEntity.status(
                    200,
                    eventos
            );

        } catch (NumberFormatException e) {

            return ResponseEntity.status(
                    400,
                    "ID inválido."
            );

        } catch (OrganizadorNaoEncontradoException e) {

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
}