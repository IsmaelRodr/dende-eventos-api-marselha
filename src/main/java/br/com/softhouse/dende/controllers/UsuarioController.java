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

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final Repositorio repositorio;

    public UsuarioController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 4: VISUALIZAR PERFIL
    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable("email") String email) {
        // Regra de negócio: buscar usuário pelo e-mail (identificador único)
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        // Regra de negócio: se não existir, retornar 404
        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado");
        }

        // Regra de negócio: calcular idade em anos, meses e dias a partir da data de nascimento
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(usuario.getDataNascimento(), hoje);
        String idade = String.format("%d anos, %d meses e %d dias",
                periodo.getYears(),
                periodo.getMonths(),
                periodo.getDays());

        // Monta o perfil com os dados solicitados
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", usuario.getNome());
        perfil.put("dataNascimento", usuario.getDataNascimento().toString());
        perfil.put("idade", idade);
        perfil.put("sexo", usuario.getSexo());
        perfil.put("email", usuario.getEmail());

        return ResponseEntity.ok(perfil);
    }

    // API 5: DESATIVAR PERFIL
    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<?> desativarUsuario(@PathVariable("email") String email) {
        // Regra de negócio: buscar usuário pelo e-mail
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        // Regra de negócio: se não existir, retornar 404
        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado!");
        }

        // Regra de negócio: verificar se a conta já está inativa
        if (!usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está inativo!");
        }

        // Aplica a desativação
        usuario.setAtivo(false);
        repositorio.salvarUsuario(usuario); // persistência pelo ID
        return ResponseEntity.status(200, "Usuário desativado com sucesso!");
    }

    // API 6: REATIVAR PERFIL
    @PatchMapping(path = "/{email}/ativar")
    public ResponseEntity<?> ativarUsuario(@PathVariable("email") String email,
                                           @RequestBody Usuario.Credenciais credenciais) {
        // Regra de negócio: a senha é obrigatória para reativação
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

        // Regra de negócio: buscar usuário pelo e-mail
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado!");
        }

        // Regra de negócio: validar a senha fornecida
        if (!usuario.getSenha().equals(credenciais.senha())) {
            return ResponseEntity.status(401, "Senha inválida.");
        }

        // Regra de negócio: verificar se a conta já está ativa
        if (usuario.isAtivo()) {
            return ResponseEntity.status(409, "Usuário já está ativo!");
        }

        // Aplica a reativação
        usuario.setAtivo(true);
        repositorio.salvarUsuario(usuario);
        return ResponseEntity.status(200, "Usuário reativado com sucesso!");
    }
}