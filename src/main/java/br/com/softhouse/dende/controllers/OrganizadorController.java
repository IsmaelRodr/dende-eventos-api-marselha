package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.LoginDto;
import br.com.softhouse.dende.dto.evento.*;
import br.com.softhouse.dende.dto.organizador.*;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.evento.*;
import br.com.softhouse.dende.exceptions.organizador.*;
import br.com.softhouse.dende.exceptions.usuario.CredenciaisInvalidasException;
import br.com.softhouse.dende.exceptions.usuario.EmailJaCadastradoException;
import br.com.softhouse.dende.exceptions.usuario.EmailInvalidoException;
import br.com.softhouse.dende.exceptions.usuario.DataNascimentoInvalidaException;
import br.com.softhouse.dende.exceptions.organizador.CnpjInvalidoException;
import br.com.softhouse.dende.exceptions.organizador.EmpresaInvalidaException;
import br.com.softhouse.dende.services.EventoService;
import br.com.softhouse.dende.services.OrganizadorService;
import java.util.List;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final OrganizadorService organizadorService;
    private final EventoService eventoService;

    public OrganizadorController() {
        this.organizadorService = new OrganizadorService();
        this.eventoService = new EventoService();
    }

    @PostMapping
    public ResponseEntity<?> cadastroOrganizador(@RequestBody CadastrarOrganizadorDto dto) {
        try {
            StatusOrganizadorDto resposta = organizadorService.cadastrar(dto);
            return ResponseEntity.status(201, resposta);
        } catch (DadosInvalidosException | DataNascimentoInvalidaException |
                 EmailInvalidoException | EmpresaInvalidaException | CnpjInvalidoException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (EmailJaCadastradoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PutMapping(path = "/{organizadorId}")
    public ResponseEntity<?> atualizarOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody AtualizarOrganizadorDto dto) {
        try {
            Long id = Long.parseLong(organizadorId);
            StatusOrganizadorDto resposta = organizadorService.atualizar(id, dto);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (OrganizadorNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (DataNascimentoInvalidaException | EmpresaInvalidaException |
                 CnpjInvalidoException | DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @GetMapping(path = "/{organizadorId}")
    public ResponseEntity<?> visualizarPerfilOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId) {
        try {
            Long id = Long.parseLong(organizadorId);
            VisualizarOrganizadorDto resposta = organizadorService.buscarPorId(id);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (OrganizadorNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PatchMapping(path = "/{organizadorId}/ativar")
    public ResponseEntity<?> ativarOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody LoginDto dto) {
        try {
            Long id = Long.parseLong(organizadorId);
            StatusOrganizadorDto resposta = organizadorService.ativar(id, dto);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (OrganizadorNaoEncontradoException | CredenciaisInvalidasException e) {
            return ResponseEntity.status(401, e.getMessage());
        } catch (OrganizadorJaAtivoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PatchMapping(path = "/{organizadorId}/desativar")
    public ResponseEntity<?> desativarOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId) {
        try {
            Long id = Long.parseLong(organizadorId);
            StatusOrganizadorDto resposta = organizadorService.desativar(id);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (OrganizadorNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (OrganizadorJaInativoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (OrganizadorComEventosAtivosException e) {
            return ResponseEntity.status(422, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> cadastrarEvento(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody CadastrarEventoDto dto) {
        try {
            Long id = parseId(organizadorId);
            if (id == null) {
                return ResponseEntity.status(400, "ID inválido.");
            }
            StatusEventoDto resposta = eventoService.cadastrarEvento(id, dto);
            return ResponseEntity.status(201, resposta);
        } catch (OrganizadorNaoEncontradoException | EventoPrincipalNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (EventoCapacidadeInvalidaException | EventoPrecoIngressoInvalidoException |
                 EventoDataInicioInvalidaException | EventoDataFimInvalidaException |
                 EventoDataFimAnteriorInicioException | EventoDuracaoInvalidaException |
                 DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<?> alterarEvento(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @PathVariable(parameter = "eventoId") String eventoId,
            @RequestBody AtualizarEventoDto dto) {
        try {
            Long organizadorIdLong = parseId(organizadorId);
            Long eventoIdLong = parseId(eventoId);
            if (organizadorIdLong == null || eventoIdLong == null) {
                return ResponseEntity.status(400, "ID inválido.");
            }
            StatusEventoDto resposta = eventoService.atualizarEvento(organizadorIdLong, eventoIdLong, dto);
            return ResponseEntity.status(200, resposta);
        } catch (EventoNaoEncontradoException | OrganizadorNaoEncontradoException |
                 EventoPrincipalNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (EventoInativoException e) {
            return ResponseEntity.status(422, e.getMessage());
        } catch (EventoCapacidadeInvalidaException | EventoPrecoIngressoInvalidoException |
                 EventoTaxaCancelamentoInvalidaException | EventoDataInicioInvalidaException |
                 EventoDataFimInvalidaException | EventoDataFimAnteriorInicioException |
                 EventoDuracaoInvalidaException | DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/ativar")
    public ResponseEntity<?> ativarEvento(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @PathVariable(parameter = "eventoId") String eventoId) {
        try {
            Long organizadorIdLong = parseId(organizadorId);
            Long eventoIdLong = parseId(eventoId);
            if (organizadorIdLong == null || eventoIdLong == null) {
                return ResponseEntity.status(400, "ID inválido.");
            }
            StatusEventoDto resposta = eventoService.ativarEvento(organizadorIdLong, eventoIdLong);
            return ResponseEntity.status(200, resposta);
        } catch (EventoNaoEncontradoException | OrganizadorNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (EventoJaAtivoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (EventoDataInicioInvalidaException | EventoDataFimInvalidaException |
                 EventoDataFimAnteriorInicioException | EventoDuracaoInvalidaException |
                 DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/desativar")
    public ResponseEntity<?> desativarEvento(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @PathVariable(parameter = "eventoId") String eventoId) {
        try {
            Long organizadorIdLong = parseId(organizadorId);
            Long eventoIdLong = parseId(eventoId);
            if (organizadorIdLong == null || eventoIdLong == null) {
                return ResponseEntity.status(400, "ID inválido.");
            }
            StatusEventoDto resposta = eventoService.desativarEvento(organizadorIdLong, eventoIdLong);
            return ResponseEntity.status(200, resposta);
        } catch (EventoNaoEncontradoException | OrganizadorNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (EventoJaInativoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEventosDoOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId) {
        try {
            Long id = Long.parseLong(organizadorId);
            List<EventosOrganizadorDto> resposta = eventoService.listarEventosOrganizador(id);
            return ResponseEntity.status(200, resposta);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        } catch (OrganizadorNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno no servidor.");
        }
    }

    private Long parseId(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}