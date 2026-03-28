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
import br.com.softhouse.dende.dto.evento.AtualizarEventoDto;
import br.com.softhouse.dende.dto.evento.CadastrarEventoDto;
import br.com.softhouse.dende.dto.evento.EventosOrganizadorDto;
import br.com.softhouse.dende.dto.evento.StatusEventoDto;
import br.com.softhouse.dende.dto.organizador.AtualizarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.CadastrarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.StatusOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.VisualizarOrganizadorDto;
import br.com.softhouse.dende.mapper.EventoMapper;
import br.com.softhouse.dende.mapper.OrganizadorMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<?> cadastroOrganizador(@RequestBody CadastrarOrganizadorDto dto) {
        Organizador organizador = OrganizadorMapper.toModel(dto);

        if (organizador == null) {
            return ResponseEntity.status(400, "Dados do organizador invalidos.");
        }

        if (organizador.getNome() == null || organizador.getNome().trim().isEmpty()
                || organizador.getEmail() == null || organizador.getEmail().trim().isEmpty()
                || organizador.getSenha() == null || organizador.getSenha().trim().isEmpty()) {
            return ResponseEntity.status(400, "Erro: Os campos obrigatorios do organizador nao podem estar vazios.");
        }

        if (organizador.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Erro: Data de nascimento e obrigatoria.");
        }

        if (organizador.getDataNascimento().isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento invalida.");
        }

        if (repositorio.emailExiste(organizador.getEmail())) {
            return ResponseEntity.status(409, "Erro de Conflito: Ja existe um organizador registado com este e-mail!");
        }

        repositorio.salvarOrganizador(organizador);
        StatusOrganizadorDto response = OrganizadorMapper.toStatusDto(
                "Organizador " + organizador.getNome() + " registado com sucesso!",
                organizador
        );
        return ResponseEntity.status(201, response);
    }

    @PutMapping(path = "/{organizadorId}")
    public ResponseEntity<?> atualizarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId,
                                                  @RequestBody AtualizarOrganizadorDto dto) {
        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "Erro: ID invalido.");
        }

        Organizador organizadorExistente = repositorio.buscarOrganizadorPorId(idNumerico);
        if (organizadorExistente == null) {
            return ResponseEntity.status(404, "Erro: Organizador nao encontrado com este ID.");
        }

        Organizador organizadorAtualizado = OrganizadorMapper.toModel(dto);
        if (organizadorAtualizado == null) {
            return ResponseEntity.status(400, "Dados do organizador invalidos.");
        }

        if (organizadorAtualizado.getNome() == null || organizadorAtualizado.getNome().trim().isEmpty()
                || organizadorAtualizado.getSenha() == null || organizadorAtualizado.getSenha().trim().isEmpty()) {
            return ResponseEntity.status(400, "Erro: Os dados atualizados nao podem estar vazios.");
        }

        repositorio.atualizarDadosOrganizador(organizadorExistente, organizadorAtualizado);
        StatusOrganizadorDto response = OrganizadorMapper.toStatusDto(
                "Perfil do organizador atualizado com sucesso!",
                organizadorExistente
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{organizadorId}")
    public ResponseEntity<?> visualizarPerfilOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId) {
        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado");
        }

        if (organizador.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Organizador possui data de nascimento invalida.");
        }

        VisualizarOrganizadorDto response = OrganizadorMapper.toVisualizarDto(organizador);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{organizadorId}/desativar")
    public ResponseEntity<?> desativarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId) {
        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado!");
        }

        if (!organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador ja esta inativo!");
        }

        List<Evento> listaEventos = repositorio.listarEventoPorOrganizador(idNumerico);
        boolean eventoEmExecucao = false;

        if (listaEventos != null) {
            for (Evento evento : listaEventos) {
                if (evento.isEventoAtivo()) {
                    eventoEmExecucao = true;
                    break;
                }
            }
        }

        if (eventoEmExecucao) {
            return ResponseEntity.status(409, "Nao e possivel desativar: este organizador possui eventos ativos.");
        }

        organizador.setAtivo(false);
        repositorio.salvarOrganizador(organizador);
        StatusOrganizadorDto response = OrganizadorMapper.toStatusDto(
                "Organizador desativado com sucesso!",
                organizador
        );
        return ResponseEntity.status(200, response);
    }

    @PatchMapping(path = "/{organizadorId}/ativar")
    public ResponseEntity<?> ativarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId,
                                               @RequestBody Organizador.Credenciais credenciais) {
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha e obrigatoria.");
        }

        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado!");
        }

        if (organizador.getSenha() == null || !organizador.getSenha().equals(credenciais.senha())) {
            return ResponseEntity.status(401, "Senha invalida.");
        }

        if (organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador ja esta ativo!");
        }

        organizador.setAtivo(true);
        repositorio.salvarOrganizador(organizador);
        StatusOrganizadorDto response = OrganizadorMapper.toStatusDto(
                "Organizador reativado com sucesso!",
                organizador
        );
        return ResponseEntity.status(200, response);
    }

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> cadastrarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                             @RequestBody CadastrarEventoDto dto) {
        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado!");
        }

        Evento evento = EventoMapper.toModel(dto);
        ResponseEntity<?> validacao = validarDatasEvento(evento);
        if (validacao != null) {
            return validacao;
        }

        if (evento.isEventoAtivo()) {
            return ResponseEntity.status(409, "Nao se pode criar um evento ativo.");
        }

        evento.setOrganizador(idNumerico);
        repositorio.salvarEvento(idNumerico, evento);

        StatusEventoDto response = EventoMapper.toStatusEventoDto("Evento criado com sucesso!", evento);
        return ResponseEntity.status(201, response);
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<?> alterarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                           @PathVariable(parameter = "eventoId") String eventoId,
                                           @RequestBody AtualizarEventoDto dto) {
        Long idNumericoOrganizador = parseId(organizadorId);
        Long idNumericoEvento = parseId(eventoId);

        if (idNumericoOrganizador == null || idNumericoEvento == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Evento evento = EventoMapper.toModel(dto);
        ResponseEntity<?> validacao = validarDatasEvento(evento);
        if (validacao != null) {
            return validacao;
        }

        Evento eventoExistente = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);
        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento nao existe!");
        }

        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento ja esta ativo!");
        }

        repositorio.atualizarEvento(idNumericoOrganizador, evento, idNumericoEvento);

        Evento eventoAtualizado = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);
        StatusEventoDto response = EventoMapper.toStatusEventoDto("Evento atualizado com sucesso!", eventoAtualizado);
        return ResponseEntity.status(200, response);
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/ativar")
    public ResponseEntity<?> ativarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                          @PathVariable(parameter = "eventoId") String eventoId) {
        Long idNumericoOrganizador = parseId(organizadorId);
        Long idNumericoEvento = parseId(eventoId);

        if (idNumericoOrganizador == null || idNumericoEvento == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Evento eventoExistente = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);
        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento nao existe!");
        }

        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento ja esta ativo!");
        }

        if (eventoExistente.getDataInicio().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(422, "Nao e possivel ativar evento com inicio anterior a data atual.");
        }

        repositorio.ativarEvento(idNumericoEvento, idNumericoOrganizador);
        StatusEventoDto response = EventoMapper.toStatusEventoDto("Evento ativado!", eventoExistente);
        return ResponseEntity.status(200, response);
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/desativar")
    public ResponseEntity<?> desativarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                             @PathVariable(parameter = "eventoId") String eventoId) {
        Long idNumericoOrganizador = parseId(organizadorId);
        Long idNumericoEvento = parseId(eventoId);

        if (idNumericoOrganizador == null || idNumericoEvento == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Evento eventoExistente = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);
        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento nao existe.");
        }

        if (!eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento ja esta desativado!");
        }

        repositorio.desativarEvento(idNumericoEvento, idNumericoOrganizador);
        StatusEventoDto response = EventoMapper.toStatusEventoDto("Evento desativado!", eventoExistente);
        return ResponseEntity.status(200, response);
    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEvento(@PathVariable(parameter = "organizadorId") String organizadorId) {
        Long idNumericoOrganizador = parseId(organizadorId);
        if (idNumericoOrganizador == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        List<EventosOrganizadorDto> listaEventos = repositorio.listarEventoPorOrganizador(idNumericoOrganizador)
                .stream()
                .map(EventoMapper::toEventosOrganizadorDto)
                .toList();

        if (listaEventos.isEmpty()) {
            return ResponseEntity.status(204, "nao ha Eventos");
        }

        return ResponseEntity.ok(listaEventos);
    }

    private Long parseId(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Evento buscarEventoDoOrganizador(Long organizadorId, Long eventoId) {
        return repositorio.listarEventoPorOrganizador(organizadorId)
                .stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .orElse(null);
    }

    private ResponseEntity<?> validarDatasEvento(Evento evento) {
        if (evento == null || evento.getDataInicio() == null || evento.getDataFim() == null) {
            return ResponseEntity.status(400, "Datas do evento sao obrigatorias.");
        }

        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataInicio = evento.getDataInicio();
        LocalDateTime dataFim = evento.getDataFim();

        if (dataInicio.isBefore(hoje)) {
            return ResponseEntity.status(422, "A data de inicio do evento nao e valida!");
        }

        if (dataFim.isBefore(hoje)) {
            return ResponseEntity.status(422, "A data de fim nao pode ser anterior a data atual.");
        }

        if (dataFim.isBefore(dataInicio)) {
            return ResponseEntity.status(422, "A data de fim nao pode ser anterior a data de inicio.");
        }

        long duracao = Duration.between(dataInicio, dataFim).toMinutes();
        if (duracao < 30) {
            return ResponseEntity.status(422, "Os eventos devem ter no minimo 30 minutos de duracao!");
        }

        return null;
    }
}
