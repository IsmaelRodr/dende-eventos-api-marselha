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
        return ResponseEntity.ok("Utilizador " + usuario.getNome() + " registado com sucesso! O seu ID é: " + usuario.getId());
    }

    // API 03 - Alterar Perfil do Usuário (AGORA POR ID)
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> atualizarUsuario(@PathVariable(parameter = "id") String id, @RequestBody Usuario usuarioAtualizado) {
        
        // Agora usamos o método novo do repositório para buscar pelo ID!
        Usuario usuarioExistente = repositorio.buscarUsuarioPorId(id);
        
        if (usuarioExistente == null) {
            return ResponseEntity.ok("Erro: Utilizador não encontrado com este ID.");
        }

        // A regra de negócio de bloquear a troca de e-mail continua firme e forte!
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail())) {
            return ResponseEntity.ok("Erro: Não é permitido alterar o e-mail de acesso.");
        }

        // Atualiza apenas os dados permitidos
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setSenha(usuarioAtualizado.getSenha());
        
        // Salva a atualização no "banco de dados"
        repositorio.salvarUsuario(usuarioExistente);

        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }
}