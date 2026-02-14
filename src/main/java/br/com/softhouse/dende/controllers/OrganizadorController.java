package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 02 - Cadastrar Utilizador Organizador
    @PostMapping
    public ResponseEntity<String> cadastroOrganizador(@RequestBody Organizador organizador) {
        // Regra de Negócio: Não podemos ter dois utilizadores com o mesmo e-mail
        if (repositorio.emailExiste(organizador.getEmail())) {
            return ResponseEntity.ok("Erro: Já existe um organizador registado com este e-mail!");
        }
        
        repositorio.salvarOrganizador(organizador);
        return ResponseEntity.ok("Organizador " + organizador.getNome() + " registado com sucesso! O seu ID é: " + organizador.getId());
    }

    // API 03 - Alterar Perfil do Organizador (AGORA POR ID)
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> atualizarOrganizador(@PathVariable(parameter = "id") String id, @RequestBody Organizador organizadorAtualizado) {
        
        // CORREÇÃO AQUI: Agora ele chama o método novo com "PorId" no final!
        Organizador organizadorExistente = repositorio.buscarOrganizadorPorId(id);
        
        if (organizadorExistente == null) {
            return ResponseEntity.ok("Erro: Organizador não encontrado com este ID.");
        }

        // A regra de negócio de bloquear a troca de e-mail 
        if (!organizadorExistente.getEmail().equals(organizadorAtualizado.getEmail())) {
            return ResponseEntity.ok("Erro: Não é permitido alterar o e-mail de acesso.");
        }

        // Atualiza os dados permitidos do organizador
        organizadorExistente.setNome(organizadorAtualizado.getNome());
        organizadorExistente.setSenha(organizadorAtualizado.getSenha());
        organizadorExistente.setRazaoSocial(organizadorAtualizado.getRazaoSocial());
        organizadorExistente.setNomeFantasia(organizadorAtualizado.getNomeFantasia());
        
        // Salva a atualização
        repositorio.salvarOrganizador(organizadorExistente);

        return ResponseEntity.ok("Perfil do organizador atualizado com sucesso!");
    }
}