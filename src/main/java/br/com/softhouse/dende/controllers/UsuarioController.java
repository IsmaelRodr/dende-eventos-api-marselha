package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;
import java.util.Objects;


import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final Repositorio repositorio;

    public UsuarioController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 01 - Cadastrar
    @PostMapping
    public ResponseEntity<String> cadastroUsuario(@RequestBody Usuario usuario) {

        // 1. VALIDAÇÃO DE TEXTOS VAZIOS
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty() ||
                usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() ||
                usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {

            return ResponseEntity.status(400,
                    "Erro: Os campos obrigatórios não podem estar vazios ou em branco.");
        }

        if (usuario.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Erro: Data de nascimento é obrigatória.");
        }

        if (usuario.getDataNascimento().isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento inválida.");
        }

        // 2. STATUS 409 PARA CONFLITO DE E-MAIL
        if (repositorio.emailExiste(usuario.getEmail())) {
            return ResponseEntity.status(409, "Erro de Conflito: Já existe um utilizador registado com este e-mail!");
        }

        repositorio.salvarUsuario(usuario);

        // Status 201 (Created) para novos cadastros
        return ResponseEntity.status(201, "Utilizador " + usuario.getNome() + " registado com sucesso! O seu ID é: " + usuario.getId());
    }

    // API 03 - Atualizar 
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> atualizarUsuario(@PathVariable(parameter = "id") String id, @RequestBody Usuario usuarioAtualizado) {

        // Tradutor: Transforma a String 'id' da URL no Long 'idNumerico'
        long idNumerico;

        try {
            idNumerico = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "Erro: ID inválido.");
        }

        Usuario usuarioExistente = repositorio.buscarUsuarioPorId(idNumerico);

        if (usuarioExistente == null) {
            return ResponseEntity.status(404, "Erro: Utilizador não encontrado com este ID.");
        }

        if (!Objects.equals(usuarioExistente.getEmail(), usuarioAtualizado.getEmail())) {
            return ResponseEntity.status(400, "Erro: Não é permitido alterar o e-mail de acesso.");
        }

        // --- VALIDAÇÃO DE TEXTOS VAZIOS NA ATUALIZAÇÃO (Pedido do Líder) ---
        if (usuarioAtualizado.getNome() == null || usuarioAtualizado.getNome().trim().isEmpty() ||
                usuarioAtualizado.getSenha() == null || usuarioAtualizado.getSenha().trim().isEmpty()) {

            return ResponseEntity.status(400,
                    "Erro: Os dados atualizados não podem estar vazios.");
        }
        // Atualiza os dados
        repositorio.atualizarDadosUsuario(usuarioExistente, usuarioAtualizado);

        // Status 200 (OK) para edições bem sucedidas
        return ResponseEntity.status(200, "Utilizador " + usuarioAtualizado.getNome() + " atualizado com sucesso!");
    }

    // API 4: VISUALIZAR PERFIL
    @GetMapping(path = "/{usuarioId}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "usuarioId") String usuarioId) {
        // Regra de negócio: buscar usuário pelo e-mail (identificador único)
        long idNumerico;

        try {
            idNumerico = Long.parseLong(usuarioId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Usuario usuario = repositorio.buscarUsuarioPorId(idNumerico);

        // Regra de negócio: se não existir, retornar 404
        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado");
        }

        if (usuario.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Usuário possui data de nascimento inválida.");
        }

        // Regra de negócio: calcular idade em anos, meses e dias a partir da data de nascimento
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(usuario.getDataNascimento(), hoje);
        String idade = String.format("%d anos, %d meses e %d dias",
                periodo.getYears(),
                periodo.getMonths(),
                periodo.getDays());

        // Monta o perfil com os dados solicitados
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", usuario.getNome());
        perfil.put("dataNascimento", usuario.getDataNascimento().toString());
        perfil.put("idade", idade);
        perfil.put("sexo", usuario.getSexo());
        perfil.put("email", usuario.getEmail());

        return ResponseEntity.ok(perfil);
    }

    // API 5: DESATIVAR PERFIL
    @PatchMapping(path = "/{usuarioId}/desativar")
    public ResponseEntity<?> desativarUsuario(@PathVariable(parameter = "usuarioId") String usuarioId) {
        // Regra de negócio: buscar usuário pelo e-mail
        long idNumerico;

        try {
            idNumerico = Long.parseLong(usuarioId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Usuario usuario = repositorio.buscarUsuarioPorId(idNumerico);

        // Regra de negócio: se não existir, retornar 404
        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado!");
        }

        // Regra de negócio: verificar se a conta já está inativa
        if (!usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está inativo!");
        }

        // Aplica a desativação
        usuario.setAtivo(false);
        repositorio.salvarUsuario(usuario); // persistência pelo ID
        return ResponseEntity.status(200, "Usuário desativado com sucesso!");
    }

    // API 6: REATIVAR PERFIL
    @PatchMapping(path = "/{usuarioId}/ativar")
    public ResponseEntity<?> ativarUsuario(@PathVariable(parameter = "usuarioId") String usuarioId,
                                           @RequestBody Usuario.Credenciais credenciais) {
        // Regra de negócio: a senha é obrigatória para reativação
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

        // Regra de negócio: buscar usuário pelo e-mail
        long idNumerico;

        try {
            idNumerico = Long.parseLong(usuarioId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Usuario usuario = repositorio.buscarUsuarioPorId(idNumerico);

        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado!");
        }

        // Regra de negócio: validar a senha fornecida
        if (usuario.getSenha() == null || !usuario.getSenha().equals(credenciais.senha())) {
            return ResponseEntity.status(401, "Senha inválida.");
        }

        // Regra de negócio: verificar se a conta já está ativa
        if (usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está ativo!");
        }

        // Aplica a reativação
        usuario.setAtivo(true);
        repositorio.salvarUsuario(usuario);
        return ResponseEntity.status(200, "Usuário reativado com sucesso!");
    }

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
            return ResponseEntity.status(200, "Ingresso cancelado com sucesso");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422, e.getMessage()); // 422 Unprocessable Entity
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }

    // US 15: GET /usuarios/{usuarioId}/ingressos
    @GetMapping(path = "/{usuarioId}/ingressos")
    public ResponseEntity<?> listarIngressos(@PathVariable(parameter = "usuarioId") String usuarioIdString) {
        try {
            long usuarioId = Long.parseLong(usuarioIdString);
            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            if (usuario == null) return ResponseEntity.status(404, "Usuário não encontrado");

            List<Ingresso> ingressos = repositorio.listarIngressosUsuario(usuarioId);
            List<Map<String, Object>> lista = ingressos.stream().map(i -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", i.getId());
                map.put("eventoNome", i.getEvento().getNome());
                map.put("dataInicio", i.getEvento().getDataInicio());
                map.put("status", i.getStatus());
                map.put("valorPago", i.getValorPago());
                map.put("eventoAtivo", i.getEvento().isEventoAtivo());
                return map;
            }).toList();

            return ResponseEntity.ok(lista);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }
}