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

            @PathVariable(parameter = "organizadorId")
            String organizadorIdString,

            @PathVariable(parameter = "eventoId")
            String eventoIdString,

            @RequestBody
            Map<String, Long> request
    ) {

        try {

            Long.parseLong(organizadorIdString);

            Long eventoId =
                    Long.parseLong(eventoIdString);

            if (request == null
                    || request.get("usuarioId") == null) {

                throw new DadosInvalidosException(
                        "usuarioId é obrigatório."
                );
            }

            Long usuarioId =
                    request.get("usuarioId");

            Map<String, Object> resultado =
                    ingressoService.comprarIngresso(
                            usuarioId,
                            eventoId
                    );

            Ingresso ingresso =
                    (Ingresso) resultado.get("ingresso");

            Double valorTotal =
                    (Double) resultado.get("valorTotal");

            List<IngressoGeradoDto> ingressosDto =
                    ingressoService
                            .listarIngressosUsuario(usuarioId)
                            .stream()
                            .filter(i ->
                                    i.getDataCompra()
                                            .equals(
                                                    ingresso.getDataCompra()
                                            )
                            )
                            .map(IngressoMapper::toGeradoDto)
                            .toList();

            ResultadoCompraIngressoDto resposta =
                    new ResultadoCompraIngressoDto(
                            valorTotal,
                            ingressosDto
                    );

            return ResponseEntity.status(
                    201,
                    resposta
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

        } catch (UsuarioNaoEncontradoException
                 | EventoNaoEncontradoException
                 | IngressoNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (CompraIngressoException
                 | CancelamentoNaoPermitidoException e) {

            return ResponseEntity.status(
                    409,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno do servidor."
            );
        }
    }

    @PostMapping(
            path = "/usuarios/{usuarioId}/{ingressoId}/cancelar"
    )
    public ResponseEntity<?> cancelarIngresso(

            @PathVariable(parameter = "usuarioId")
            String usuarioIdString,

            @PathVariable(parameter = "ingressoId")
            String ingressoIdString
    ) {

        try {

            Long usuarioId =
                    Long.parseLong(usuarioIdString);

            Long ingressoId =
                    Long.parseLong(ingressoIdString);

            ingressoService.cancelarIngresso(
                    usuarioId,
                    ingressoId
            );

            Ingresso ingresso =
                    ingressoService
                            .listarIngressosUsuario(usuarioId)
                            .stream()
                            .filter(i ->
                                    i.getId().equals(ingressoId)
                            )
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IngressoNaoEncontradoException(
                                                    "Ingresso não encontrado."
                                            )
                            );

            CancelarIngressoUsuarioDto resposta =
                    UsuarioMapper.toCancelarDTO(
                            "Ingresso cancelado com sucesso.",
                            ingresso
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

        } catch (DadosInvalidosException e) {

            return ResponseEntity.status(
                    400,
                    e.getMessage()
            );

        } catch (UsuarioNaoEncontradoException
                 | EventoNaoEncontradoException
                 | IngressoNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
                    e.getMessage()
            );

        } catch (CancelamentoNaoPermitidoException e) {

            return ResponseEntity.status(
                    409,
                    e.getMessage()
            );

        } catch (Exception e) {

            return ResponseEntity.status(
                    500,
                    "Erro interno do servidor."
            );
        }
    }

    @GetMapping(
            path = "/usuarios/{usuarioId}"
    )
    public ResponseEntity<?> listarIngressos(

            @PathVariable(parameter = "usuarioId")
            String usuarioIdString
    ) {

        try {

            Long usuarioId =
                    Long.parseLong(usuarioIdString);

            List<ListaIngressosUsuarioDto> lista =
                    ingressoService
                            .listarIngressosUsuario(usuarioId)
                            .stream()
                            .map(IngressoMapper::toListaUsuarioDto)
                            .toList();

            return ResponseEntity.status(
                    200,
                    lista
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

        } catch (UsuarioNaoEncontradoException e) {

            return ResponseEntity.status(
                    404,
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