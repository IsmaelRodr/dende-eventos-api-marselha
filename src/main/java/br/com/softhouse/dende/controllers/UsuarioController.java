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

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final Repositorio repositorio;

    public UsuarioController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 01 - Cadastrar Utilizador Comum
    @PostMapping
    public ResponseEntity<String> cadastroUsuario(@RequestBody Usuario usuario) {
        // Regra de Negócio: Não podemos ter dois utilizadores com o mesmo e-mail
        if (repositorio.emailExiste(usuario.getEmail())) {
            return ResponseEntity.ok("Erro: Já existe um utilizador registado com este e-mail!");
        }
        
        repositorio.salvarUsuario(usuario);
        return ResponseEntity.ok("Utilizador " + usuario.getNome() + " registado com sucesso!");
    }

    // API 03 - Alterar Perfil do Utilizador Comum
    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarUsuario(@PathVariable(parameter = "email") String email, @RequestBody Usuario usuarioAtualizado) {
        Usuario usuarioExistente = repositorio.buscarUsuario(email);
        
        if (usuarioExistente == null) {
            return ResponseEntity.ok("Erro: Utilizador não encontrado.");
        }
        
        // Regra de Negócio: Não é permitido modificar o e-mail
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail())) {
            return ResponseEntity.ok("Erro: Não é permitido alterar o e-mail de acesso.");
        }
        
        // Atualiza os dados permitidos
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setDataNascimento(usuarioAtualizado.getDataNascimento());
        usuarioExistente.setSexo(usuarioAtualizado.getSexo());
        usuarioExistente.setSenha(usuarioAtualizado.getSenha());
        
        // Guarda a atualização
        repositorio.salvarUsuario(usuarioExistente);
        return ResponseEntity.ok("Perfil de " + usuarioExistente.getNome() + " atualizado com sucesso!");
    }
}