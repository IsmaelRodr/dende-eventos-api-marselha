package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    // API 04 Visualizar Perfil de Usuário Organizador
    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfilOrganizador(@PathVariable(parameter = "email") String email) {
        Organizador organizador = repositorio.buscarOrganizador(email);

        if (organizador == null) {
            return ResponseEntity.ok("Organizador não encontrado");
        }

        // Regra de Négocio: Data de Nascimento Formatada em Anos-Meses-Dias.
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(organizador.getDataNascimento(), hoje);
        String idade = String.format("%d anos, %d meses e %d dias",
                periodo.getYears(),
                periodo.getMonths(),
                periodo.getDays());

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nome", organizador.getNome());
        perfil.put("dataNascimento", organizador.getDataNascimento().toString());
        perfil.put("idade", idade);
        perfil.put("sexo", organizador.getSexo());
        perfil.put("email", organizador.getEmail());

        // Regra de Négocio: Retorna dados de uma empresa associada a um organizador, caso ela exista.
        if(organizador.getCnpj() != null ){
            perfil.put("cnpj", organizador.getCnpj());
            perfil.put("razaoSocial", organizador.getRazaoSocial());
            perfil.put("nomeFantasia", organizador.getNomeFantasia());
        }

        return ResponseEntity.ok(perfil);
    }

    // API 05 Desativa Perfil de Organizdor

    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<?> desativarOrganizador(@PathVariable(parameter = "email") String email) {
        Organizador organizador = repositorio.buscarOrganizador(email);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        if (!organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador já está inativo!");
        }

        // Reagra de Negócio: Verifica se há eventos ativos associados ao organizador
        List<Evento> listaEventos = repositorio.listarEventoOrganizador(organizador);
        boolean eventoEmExecucao = false;

        if (listaEventos != null) {
            for (Evento evento : listaEventos) {
                if (evento.isEventoAtivo()) {
                    eventoEmExecucao = true;
                    break;
                }
            }
        }

        if (eventoEmExecucao) {
            return ResponseEntity.status(409, "Não é possível desativar: este organizador possui eventos ativos.");
        }

        organizador.setAtivo(false);
        repositorio.salvarOrganizador(organizador);
        return ResponseEntity.status(200, "Organizador desativado com sucesso!");
    }

    // API 06 Reativar Perfil de Usuário Organizador
    @PatchMapping(path = "/{email}/ativar")
    public ResponseEntity<?> ativarOrganizador(@PathVariable(parameter = "email") String email,
                                               @RequestBody Organizador.Credenciais credenciais) {

        // Regra de Negócio: Validação de email e senha
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

        Organizador organizador = repositorio.buscarOrganizador(email);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        if (!organizador.getSenha().equals(credenciais.senha())) {
            return ResponseEntity.status(401, "Senha inválida.");
        }

        if (organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador já está ativo!");
        }

        organizador.setAtivo(true);
        repositorio.salvarOrganizador(organizador);

        return ResponseEntity.status(200, "Organizador reativado com sucesso!");
    }

}
