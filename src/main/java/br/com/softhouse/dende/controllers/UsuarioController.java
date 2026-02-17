package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;



    // API 04 Visualizar Perfil de Usuario Comum
    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        Usuario usuario = repositorio.buscarUsuario(email);

        if (usuario == null) {
            return ResponseEntity.ok("Usuário não encontrado");
        }

        // Regra de Négocio: Data de Nascimento Formatada em Anos-Meses-Dias.
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(usuario.getDataNascimento(), hoje);
        String idade = String.format("%d anos, %d meses e %d dias",
                periodo.getYears(),
                periodo.getMonths(),
                periodo.getDays());

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", usuario.getNome());
        perfil.put("dataNascimento", usuario.getDataNascimento().toString());
        perfil.put("idade", idade);
        perfil.put("sexo", usuario.getSexo());
        perfil.put("email", usuario.getEmail());

        return ResponseEntity.ok(perfil);
    }

    // API 05 Desativa Perfil de Usuário
    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<?> desativarUsuario (@PathVariable( parameter = "email") String email){
        Usuario usuario = repositorio.buscarUsuario(email);
        if(usuario != null){
            if(! usuario.isAtivo()){
                return ResponseEntity.status(409,"Usuário já está Inativo!");
            } else {
                usuario.setAtivo(false);
                repositorio.salvarUsuario(usuario);
                return ResponseEntity.status(200,"Usuário Desativado!");
            }
        } else {
            return ResponseEntity.status(404,"Usuário Não Encontrado!");

        }
    }

    // API 06  Reativar Perfil de Usuário
    @PatchMapping(path = "/{email}/ativar")
    public ResponseEntity<?> ativarUsuario (@PathVariable(parameter = "email") String email,
                                            @RequestBody Usuario.Credenciais credenciais){


        // Regra de Negócio: Validação de email e senha
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

        Usuario usuario = repositorio.buscarUsuario(email);

        if(usuario != null){
            if (!usuario.getSenha().equals(credenciais.senha())) {
                return ResponseEntity.status(401, "Senha inválida.");
            }
            if(usuario.isAtivo()){
                return ResponseEntity.status(409,"Usuário já está Ativo!");
            } else {
                usuario.setAtivo(true);
                repositorio.salvarUsuario(usuario);
                return ResponseEntity.status(200,"Usuário Reativado!");
            }
        } else {
            return ResponseEntity.status(404,"Usuário Não Encontrado!");
        }
    }
}