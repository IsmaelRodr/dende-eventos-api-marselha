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
        
        // 1. VALIDAÇÃO DE TEXTOS VAZIOS (Apenas os dados da Pessoa Física, a Empresa é opcional)
        if (organizador.getNome().trim().isEmpty() || 
            organizador.getEmail().trim().isEmpty() || 
            organizador.getSenha().trim().isEmpty()) {
            return ResponseEntity.status(400, "Erro: Os campos obrigatórios do organizador não podem estar vazios.");
        }

        // 2. STATUS 409 PARA CONFLITO DE E-MAIL 
        if (repositorio.emailExiste(organizador.getEmail())) {
            return ResponseEntity.status(409, "Erro de Conflito: Já existe um organizador registado com este e-mail!");
        }

        repositorio.salvarOrganizador(organizador);
        
        return ResponseEntity.status(201, "Organizador " + organizador.getNome() + " registado com sucesso! O seu ID é: " + organizador.getId());
    }

    // API 03 - Alterar Perfil do Organizador (AGORA POR ID)
   @PutMapping(path = "/{id}")
    public ResponseEntity<String> atualizarOrganizador(@PathVariable(parameter = "id") String id, @RequestBody Organizador organizadorAtualizado) {
        
        // 1. AQUI ESTÁ O TRADUTOR DE STRING PARA LONG
        Long idNumerico = Long.parseLong(id);
        
        // 2. Busca pelo número
        Organizador organizadorExistente = repositorio.buscarOrganizadorPorId(idNumerico);
        
        if (organizadorExistente == null) {
            return ResponseEntity.status(404, "Erro: Organizador não encontrado com este ID.");
        }

        if (!organizadorExistente.getEmail().equals(organizadorAtualizado.getEmail())) {
            return ResponseEntity.status(400, "Erro: Não é permitido alterar o e-mail de acesso.");
        }

        // Atualiza os dados usando o método novo
        repositorio.atualizarDadosOrganizador(organizadorExistente, organizadorAtualizado);

        return ResponseEntity.ok("Perfil do organizador atualizado com sucesso!");
    }
}