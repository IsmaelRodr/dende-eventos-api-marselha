package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Evento; // Será criado futuramente
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 4: VISUALIZAR PERFIL
    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfilOrganizador(@PathVariable("email") String email) {
        // Regra de negócio: buscar organizador pelo e-mail
        Organizador organizador = repositorio.buscarOrganizadorPorEmail(email);

        // Regra de negócio: se não existir, retornar 404
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado");
        }

        // Regra de negócio: calcular idade em anos, meses e dias
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(organizador.getDataNascimento(), hoje);
        String idade = String.format("%d anos, %d meses e %d dias",
                periodo.getYears(),
                periodo.getMonths(),
                periodo.getDays());

        // Monta o perfil com dados pessoais
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", organizador.getNome());
        perfil.put("dataNascimento", organizador.getDataNascimento().toString());
        perfil.put("idade", idade);
        perfil.put("sexo", organizador.getSexo());
        perfil.put("email", organizador.getEmail());

        // Regra de negócio: se houver empresa associada, exibir seus dados
        if (organizador.getEmpresa() != null) {
            perfil.put("cnpj", organizador.getEmpresa().getCnpj());
            perfil.put("razaoSocial", organizador.getEmpresa().getRazaoSocial());
            perfil.put("nomeFantasia", organizador.getEmpresa().getNomeFantasia());
        }

        return ResponseEntity.ok(perfil);
    }

    // API 5: DESATIVAR PERFIL
    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<?> desativarOrganizador(@PathVariable("email") String email) {
        // Regra de negócio: buscar organizador pelo e-mail
        Organizador organizador = repositorio.buscarOrganizadorPorEmail(email);

        // Regra de negócio: se não existir, retornar 404
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        // Regra de negócio: verificar se já está inativo
        if (!organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador já está inativo!");
        }

        // Regra de negócio : só pode desativar se não tiver eventos ativos ou em execução
        List<Evento> listaEventos = repositorio.listarEventosPorOrganizadorId(organizador.getId()); // método a ser implementado
        boolean eventoEmExecucao = false;

        if (listaEventos != null) {
            for (Evento evento : listaEventos) {
                if (evento.isEventoAtivo()) { // verifica se o evento está ativo
                    eventoEmExecucao = true;
                    break;
                }
            }
        }

        if (eventoEmExecucao) {
            return ResponseEntity.status(409, "Não é possível desativar: este organizador possui eventos ativos.");
        }

        // Aplica a desativação
        organizador.setAtivo(false);
        repositorio.salvarOrganizador(organizador);
        return ResponseEntity.status(200, "Organizador desativado com sucesso!");
    }

    // ==================== STORY 6: REATIVAR PERFIL ====================
    @PatchMapping(path = "/{email}/ativar")
    public ResponseEntity<?> ativarOrganizador(@PathVariable("email") String email,
                                               @RequestBody Organizador.Credenciais credenciais) {
        // Regra de negócio: senha obrigatória para reativação
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

        // Regra de negócio: buscar organizador pelo e-mail
        Organizador organizador = repositorio.buscarOrganizadorPorEmail(email);
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        // Regra de negócio: validar a senha fornecida
        if (!organizador.getSenha().equals(credenciais.senha())) {
            return ResponseEntity.status(401, "Senha inválida.");
        }

        // Regra de negócio: verificar se a conta já está ativa
        if (organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador já está ativo!");
        }

        // Aplica a reativação
        organizador.setAtivo(true);
        repositorio.salvarOrganizador(organizador);
        return ResponseEntity.status(200, "Organizador reativado com sucesso!");
    }
}