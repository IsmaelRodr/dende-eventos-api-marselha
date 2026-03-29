package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.ingresso.ResultadoCompraIngressoDto;
import br.com.softhouse.dende.dto.usuario.CancelarIngressoUsuarioDto;
import br.com.softhouse.dende.mapper.UsuarioMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

// Import dos DTOs e Mappers
import br.com.softhouse.dende.dto.usuario.ListaIngressosUsuarioDto;
import br.com.softhouse.dende.dto.ingresso.IngressoGeradoDto;
import br.com.softhouse.dende.mapper.IngressoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
// Removemos o RequestMapping geral para podermos usar caminhos absolutos abaixo
public class IngressoController {

    private final Repositorio repositorio;

    public IngressoController() {
        this.repositorio = Repositorio.getInstance();
    }

    // 1. Rota de Comprar Ingresso (Que trouxemos do OrganizadorController)
    @PostMapping(path = "/organizadores/{organizadorId}/eventos/{eventoId}/ingressos")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "organizadorId") String organizadorIdString,
            @PathVariable(parameter = "eventoId") String eventoIdString,
            @RequestBody Map<String, Long> request) {

        try {
            long organizadorId = Long.parseLong(organizadorIdString);
            long eventoId = Long.parseLong(eventoIdString);

            if (request == null || request.get("usuarioId") == null) {
                return ResponseEntity.status(400, "Body inválido: usuarioId obrigatório");
            }
            long usuarioId = ((Number) request.get("usuarioId")).longValue();

            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            if (usuario == null || !usuario.isAtivo()) {
                return ResponseEntity.status(404, "Usuário não encontrado ou inativo");
            }

            List<Evento> eventosOrganizador = repositorio.listarEventoPorOrganizador(organizadorId);
            Evento evento = eventosOrganizador.stream().filter(e -> e.getId().equals(eventoId)).findFirst().orElse(null);
            if (evento == null) {
                return ResponseEntity.status(404, "Evento não encontrado");
            }
            if (!evento.isEventoAtivo()) {
                return ResponseEntity.status(422, "Evento inativo");
            }
            if (evento.getIngressosDisponiveis() <= 0) {
                return ResponseEntity.status(409, "Vagas esgotadas");
            }
            if (evento.getDataInicio().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(422, "Evento expirado");
            }

            Map<String, Object> resultado = repositorio.comprarIngresso(usuarioId, eventoId);
            if (resultado == null) {
                return ResponseEntity.status(409, "Falha na compra");
            }

            Ingresso ingresso = (Ingresso) resultado.get("ingresso");
            double valorTotal = (Double) resultado.get("valorTotal");

            List<IngressoGeradoDto> ingressosDto =
                    repositorio.listarIngressosUsuario(usuarioId).stream()
                            .filter(i -> i.getDataCompra().equals(ingresso.getDataCompra()))
                            .map(IngressoMapper::toGeradoDto)
                            .toList();

            ResultadoCompraIngressoDto resposta = new ResultadoCompraIngressoDto(valorTotal, ingressosDto);

            return ResponseEntity.status(201, resposta);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }

    // 2. Rota de Cancelar Ingresso (Que estava no UsuarioController)
    @PostMapping(path = "/usuarios/{usuarioId}/ingressos/{ingressoId}")
    public ResponseEntity<?> cancelarIngresso(
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

            List<Ingresso> ingressos = repositorio.listarIngressosUsuario(usuarioId);
            Ingresso ingressoExistente = null;

            for (Ingresso ingresso : ingressos) {
                if (ingresso.getId().equals(ingressoId)) {
                    ingressoExistente = ingresso;
                    break;
                }
            }

            if (ingressoExistente == null) {
                return ResponseEntity.status(404, "Ingresso não encontrado para este usuário.");
            }


            CancelarIngressoUsuarioDto resposta = UsuarioMapper.toCancelarDTO(
                    "Ingresso cancelado com sucesso e o valor foi estornado.", ingressoExistente);

            return ResponseEntity.status(200, resposta);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422, e.getMessage()); // 422 Unprocessable Entity
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }

    // 3. Rota de Listar Ingressos - US 15 (Que estava no UsuarioController)
    @GetMapping(path = "/usuarios/{usuarioId}/ingressos")
    public ResponseEntity<?> listarIngressos(@PathVariable(parameter = "usuarioId") String usuarioIdString) {
        try {
            long usuarioId = Long.parseLong(usuarioIdString);
            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            if (usuario == null) return ResponseEntity.status(404, "Usuário não encontrado");

            List<Ingresso> ingressos = repositorio.listarIngressosUsuario(usuarioId);

            // ALTERADO: Retorna a lista de DTOs mapeados
            List<ListaIngressosUsuarioDto> lista = ingressos.stream()
                    .map(IngressoMapper::toListaUsuarioDto)
                    .toList();

            return ResponseEntity.status(200, lista);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }
}