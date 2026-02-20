package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;
import java.util.Objects;


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
}