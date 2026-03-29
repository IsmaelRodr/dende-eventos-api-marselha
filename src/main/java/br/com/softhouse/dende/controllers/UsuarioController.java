package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;

import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.usuario.StatusUsuarioDto;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

// IMPORTS NOVOS DOS MAPPERS E DTOS
import br.com.softhouse.dende.mapper.UsuarioMapper;
import br.com.softhouse.dende.dto.usuario.CadastrarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.AtualizarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.VisualizarUsuarioDto;

import java.time.LocalDate;

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
    public ResponseEntity<?> cadastroUsuario(@RequestBody CadastrarUsuarioDto dto) {

        // Conversão do DTO para o modelo
        Usuario usuario = UsuarioMapper.toModel(dto);

        // Validação de objeto nulo
        if (usuario == null) {
            return ResponseEntity.status(400, "Dados do utilizador invalidos.");
        }

        // Validação dos campos obrigatórios
        if (usuario.getNome() == null || usuario.getNome().isBlank() ||
                usuario.getEmail() == null || usuario.getEmail().isBlank() ||
                usuario.getSenha() == null || usuario.getSenha().isBlank() ||
                usuario.getSexo() == null || usuario.getSexo().isBlank()) {

            return ResponseEntity.status(400,
                    "Erro: Os campos obrigatorios nao podem estar vazios.");
        }

        // Validação de data
        if (usuario.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Erro: Data de nascimento e obrigatoria.");
        }

        if (usuario.getDataNascimento().isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento invalida.");
        }

        // Validação de formato de email
        if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.status(400, "Email inválido.");
        }

        // Validação de unicidade
        if (repositorio.emailExiste(usuario.getEmail())) {
            return ResponseEntity.status(409,
                    "Erro de Conflito: Ja existe um utilizador registado com este e-mail!");
        }

        // Persistência
        repositorio.salvarUsuario(usuario);

        StatusUsuarioDto resposta = UsuarioMapper.toStatusDto("Utilizador " + usuario.getNome() +
                " registado com sucesso! ", usuario);
        // Retorno
        return ResponseEntity.status(201,resposta);
    }

    // API 03 - Atualizar 
    // ALTERADO: Recebe o DTO em vez do Model
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable(parameter = "id") String id,
            @RequestBody AtualizarUsuarioDto dto
            //@RequestBody Usuario.Credenciais credencial
            ) {

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        // Conversão de identificador do tipo caractere para numerico
        long idNumerico;
        try {
            idNumerico = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "Erro: ID invalido.");
        }

        // Busca do objeto na persistencia
        Usuario usuarioExistente = repositorio.buscarUsuarioPorId(idNumerico);

        //validação da presença do objeto
        if (usuarioExistente == null) {
            return ResponseEntity.status(404,
                    "Erro: Utilizador nao encontrado com este ID.");
        }

        // Valida o usuario e senha
        /*if (    !credencial.email().equals(usuarioExistente.getEmail())
                || !credencial.senha().equals(usuarioExistente.getSenha())){
            return ResponseEntity.status(401, "Credenciais de acesso inválida.");
        }*/

        // Conversão do DTO de requisição para objeto de dominio
        Usuario usuarioAtualizado = UsuarioMapper.toModel(dto);

        //Verificação da presença de valores no objeto de requisição
        if (usuarioAtualizado == null) {
            return ResponseEntity.status(400, "Dados do utilizador invalidos.");
        }

        //O bloco abaixo valida os valores nos atributos do objeto de requisição
        String nome = usuarioAtualizado.getNome();
        if (nome != null && nome.isBlank()) {
            return ResponseEntity.status(400, "Nome inválido.");
        }

        String senha = usuarioAtualizado.getSenha();
        if (senha != null && senha.isBlank()) {
            return ResponseEntity.status(400, "Senha inválida.");
        }

        String sexo = usuarioAtualizado.getSexo();
        if (sexo != null && sexo.isBlank()) {
            return ResponseEntity.status(400, "Sexo inválido.");
        }

        LocalDate dataNascimento = usuarioAtualizado.getDataNascimento();
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento invalida");
        }

        //chamada do metodo para persistencia
        repositorio.atualizarDadosUsuario(usuarioExistente, usuarioAtualizado);

        StatusUsuarioDto resposta = UsuarioMapper.toStatusDto(
                "Perfil do usuario atualizado com sucesso!",
                usuarioExistente
        );

        //Retorno ao Cliente da ação requerida
        return ResponseEntity.status(200,resposta);
    }

    // API 4: VISUALIZAR PERFIL
    @GetMapping(path = "/{usuarioId}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "usuarioId") String usuarioId
                                              //@RequestBody Usuario.Credenciais credencial
                                              ) {

       /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

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

        // Valida o usuario e senha
        /*if (    !credencial.email().equals(usuario.getEmail())
                || !credencial.senha().equals(usuario.getSenha())){
            return ResponseEntity.status(401, "Credenciais de acesso inválida.");
        }*/


        if (usuario.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Usuário possui data de nascimento inválida.");
        }

        // ALTERADO: Remover a montagem manual do HashMap e utilizar o DTO da sua equipe
        VisualizarUsuarioDto response = UsuarioMapper.toVisualizarDto(usuario);
        
        return ResponseEntity.ok(response);
    }

    // API 5: DESATIVAR PERFIL (Fica Intacto)
    @PatchMapping(path = "/{usuarioId}/desativar")
    public ResponseEntity<?> desativarUsuario(@PathVariable(parameter = "usuarioId") String usuarioId
                                              //@RequestBody Usuario.Credenciais credencial
                                              ) {

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

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

        // Valida o usuario e senha
        /*if (    !credencial.email().equals(usuario.getEmail())
                || !credencial.senha().equals(usuario.getSenha())){
            return ResponseEntity.status(401, "Credenciais de acesso inválida.");
        }*/

        if (!usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está inativo!");
        }

        StatusUsuarioDto resposta = UsuarioMapper.toStatusDto("Usuário desativado com sucesso!", usuario);

        usuario.setAtivo(false);
        repositorio.salvarUsuario(usuario); 
        return ResponseEntity.status(200, resposta);
    }

    // API 6: REATIVAR PERFIL (Fica Intacto)
    @PatchMapping(path = "/{usuarioId}/ativar")
    public ResponseEntity<?> ativarUsuario(@PathVariable(parameter = "usuarioId") String usuarioId
                                           //@RequestBody Usuario.Credenciais credencial
                                           ) {

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

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

        // Valida o usuario e senha
        /*if (    !credencial.email().equals(usuario.getEmail())
                || !credencial.senha().equals(usuario.getSenha())){
            return ResponseEntity.status(401, "Credenciais de acesso inválida.");
        }*/

        if (usuario.isAtivo()) {
            return ResponseEntity.status( 409, "Usuário já está ativo!");
        }

        StatusUsuarioDto resposta = UsuarioMapper.toStatusDto("Usuário desativado com sucesso!", usuario);

        usuario.setAtivo(true);
        repositorio.salvarUsuario(usuario);
        return ResponseEntity.status(200, resposta);
    }
}