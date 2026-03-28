package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

// IMPORTS NOVOS DOS MAPPERS E DTOS
import br.com.softhouse.dende.mapper.UsuarioMapper;
import br.com.softhouse.dende.dto.usuario.CadastrarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.AtualizarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.VisualizarUsuarioDto;

import java.time.LocalDate;
import java.util.Objects;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final Repositorio repositorio;

    public UsuarioController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 01 - Cadastrar
    // ALTERADO: Recebe o DTO em vez do Model
    @PostMapping
    public ResponseEntity<String> cadastroUsuario(@RequestBody CadastrarUsuarioDto dto) {

        // ALTERADO: Usamos o Mapper para converter o DTO no Usuario, assim podemos manter as suas validações originais
        Usuario usuario = UsuarioMapper.toModel(dto);

        // 1. VALIDAÇÃO DE TEXTOS VAZIOS (Mantido exatamente como você fez)
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
    // ALTERADO: Recebe o DTO em vez do Model
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> atualizarUsuario(@PathVariable(parameter = "id") String id, @RequestBody AtualizarUsuarioDto dto) {

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

        // ALTERADO: Usamos o Mapper para converter
        Usuario usuarioAtualizado = UsuarioMapper.toModel(dto);

        // O Restante é a sua lógica original intacta
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

        // ALTERADO: Remover a montagem manual do HashMap e utilizar o DTO da sua equipe
        VisualizarUsuarioDto response = UsuarioMapper.toVisualizarDto(usuario);
        
        return ResponseEntity.ok(response);
    }

    // API 5: DESATIVAR PERFIL (Fica Intacto)
    @PatchMapping(path = "/{usuarioId}/desativar")
    public ResponseEntity<?> desativarUsuario(@PathVariable(parameter = "usuarioId") String usuarioId) {
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

        if (!usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está inativo!");
        }

        usuario.setAtivo(false);
        repositorio.salvarUsuario(usuario); 
        return ResponseEntity.status(200, "Usuário desativado com sucesso!");
    }

    // API 6: REATIVAR PERFIL (Fica Intacto)
    @PatchMapping(path = "/{usuarioId}/ativar")
    public ResponseEntity<?> ativarUsuario(@PathVariable(parameter = "usuarioId") String usuarioId,
                                           @RequestBody Usuario.Credenciais credenciais) {
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

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

        if (usuario.getSenha() == null || !usuario.getSenha().equals(credenciais.senha())) {
            return ResponseEntity.status(401, "Senha inválida.");
        }

        if (usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está ativo!");
        }

        usuario.setAtivo(true);
        repositorio.salvarUsuario(usuario);
        return ResponseEntity.status(200, "Usuário reativado com sucesso!");
    }
}