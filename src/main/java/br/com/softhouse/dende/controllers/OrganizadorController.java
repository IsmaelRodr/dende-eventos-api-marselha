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
        return ResponseEntity.ok("Organizador " + organizador.getNome() + " registado com sucesso!");
    }

    // API 03 - Alterar Perfil do Organizador
    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarOrganizador(@PathVariable(parameter = "email") String email, @RequestBody Organizador organizadorAtualizado) {
        Organizador organizadorExistente = repositorio.buscarOrganizador(email);
        
        if (organizadorExistente == null) {
            return ResponseEntity.ok("Erro: Organizador não encontrado.");
        }
        
        // Regra de Negócio: Não é permitido modificar o e-mail
        if (!organizadorExistente.getEmail().equals(organizadorAtualizado.getEmail())) {
            return ResponseEntity.ok("Erro: Não é permitido alterar o e-mail de acesso.");
        }
        
        // Atualiza os dados permitidos
        organizadorExistente.setNome(organizadorAtualizado.getNome());
        organizadorExistente.setDataNascimento(organizadorAtualizado.getDataNascimento());
        organizadorExistente.setSexo(organizadorAtualizado.getSexo());
        organizadorExistente.setSenha(organizadorAtualizado.getSenha());
        organizadorExistente.setCnpj(organizadorAtualizado.getCnpj());
        organizadorExistente.setRazaoSocial(organizadorAtualizado.getRazaoSocial());
        organizadorExistente.setNomeFantasia(organizadorAtualizado.getNomeFantasia());
        
        repositorio.salvarOrganizador(organizadorExistente);
        return ResponseEntity.ok("Perfil do organizador " + organizadorExistente.getNome() + " atualizado com sucesso!");
    }
}