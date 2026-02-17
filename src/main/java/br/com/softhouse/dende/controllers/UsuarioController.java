package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;



@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final Repositorio repositorio;

    public UsuarioController() {
        this.repositorio = new Repositorio();
    }

    // API 01 - Cadastro com Status 201
    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario usuario) {
        if (repositorio.emailExiste(usuario.getEmail())) {
            return ResponseEntity.status(400, "Erro: Já existe um utilizador com este e-mail!");
        }

        // Validação simples de senha curta (pedido do líder)
        if (usuario.getSenha().length() < 6) {
             return ResponseEntity.status(400, "Erro: A senha deve ter no mínimo 6 caracteres.");
        }

        repositorio.salvarUsuario(usuario);
        
        // MUDANÇA: Status 201 (Created)
        return ResponseEntity.status(201, "Utilizador criado com sucesso! ID: " + usuario.getId());
    }

    // API 03 - Atualizar (Recebendo Long e delegando para o Repositório)
   @PutMapping(path = "/{id}")
    public ResponseEntity<String> atualizarUsuario(@PathVariable(parameter = "id") String id, @RequestBody Usuario usuarioAtualizado) {
        
        // 1. AQUI ESTÁ O TRADUTOR: Transforma a String 'id' da URL no Long 'idNumerico'
        Long idNumerico = Long.parseLong(id);
        
        // 2. Agora o repositório busca usando o número!
        Usuario usuarioExistente = repositorio.buscarUsuarioPorId(idNumerico);
        
        if (usuarioExistente == null) {
            return ResponseEntity.status(404, "Erro: Utilizador não encontrado com este ID.");
        }

        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail())) {
            return ResponseEntity.status(400, "Erro: Não é permitido alterar o e-mail de acesso.");
        }

        // Atualiza os dados
        repositorio.atualizarDadosUsuario(usuarioExistente, usuarioAtualizado);

        return ResponseEntity.status(201, "Utilizador " + usuarioAtualizado.getNome() + " atualizado com sucesso! O seu ID é: " + usuarioAtualizado.getId());}
    
    // API 04 - Visualizar (Recebendo Long)
    @GetMapping(path = "/{id}")
    public ResponseEntity<String> visualizarUsuario(@PathVariable(parameter = "id") String idString) {
        Long id = Long.parseLong(idString);
        Usuario usuario = repositorio.buscarUsuarioPorId(id);
        
        if (usuario == null) {
            return ResponseEntity.status(404, "Erro: Utilizador não encontrado.");
        }
        
        // ... (o resto do código de calcular idade continua igual) ...
        // Apenas para não ficar gigante, use a lógica de Period que já fizemos
        return ResponseEntity.ok("Nome: " + usuario.getNome()); // Exemplo simplificado
    }
}