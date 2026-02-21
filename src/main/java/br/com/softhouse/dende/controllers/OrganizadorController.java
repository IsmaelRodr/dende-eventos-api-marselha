package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.Repositorio;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController(){
        this.repositorio = Repositorio.getInstance();
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/{status}")
    public ResponseEntity<String> desativarEvento(@PathVariable(parameter = "organizadorId") long organizadorId, @PathVariable(parameter = "eventoId") long eventoId, @PathVariable(parameter = "status") String status){

        Evento eventoExistente = repositorio.listarEventoOrganizador(organizadorId)
                .stream()
                .filter(e -> e.getId() == eventoId)
    // API 02 - Cadastrar Utilizador Organizador
    @PostMapping
    public ResponseEntity<String> cadastroOrganizador(@RequestBody Organizador organizador) {

        // 1. VALIDAÇÃO DE TEXTOS VAZIOS (Apenas os dados da Pessoa Física, a Empresa é opcional)
        if (organizador.getNome() == null || organizador.getNome().trim().isEmpty() ||
                organizador.getEmail() == null || organizador.getEmail().trim().isEmpty() ||
                organizador.getSenha() == null || organizador.getSenha().trim().isEmpty()) {

            return ResponseEntity.status(400,
                    "Erro: Os campos obrigatórios do organizador não podem estar vazios.");
        }

        if (organizador.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Erro: Data de nascimento é obrigatória.");
        }

        if (organizador.getDataNascimento().isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento inválida.");
        }

        // 2. STATUS 409 PARA CONFLITO DE E-MAIL 
        if (repositorio.emailExiste(organizador.getEmail())) {
            return ResponseEntity.status(409, "Erro de Conflito: Já existe um organizador registado com este e-mail!");
        }

        repositorio.salvarOrganizador(organizador);

        return ResponseEntity.status(201, "Organizador " + organizador.getNome() + " registado com sucesso! O seu ID é: " + organizador.getId());
    }

    // API 03 - Alterar Perfil do Organizador (AGORA POR ID)
    @PutMapping(path = "/{organizadorId}")
    public ResponseEntity<String> atualizarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId, @RequestBody Organizador organizadorAtualizado) {

        // 1. AQUI ESTÁ O TRADUTOR DE STRING PARA LONG
        long idNumerico;

        try {
            idNumerico = Long.parseLong(organizadorId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "Erro: ID inválido.");
        }

        // 2. Busca pelo número
        Organizador organizadorExistente = repositorio.buscarOrganizadorPorId(idNumerico);

        if (organizadorExistente == null) {
            return ResponseEntity.status(404, "Erro: Organizador não encontrado com este ID.");
        }

        if (!Objects.equals(organizadorExistente.getEmail(), organizadorAtualizado.getEmail())) {
            return ResponseEntity.status(400, "Erro: Não é permitido alterar o e-mail de acesso.");
        }

        if (organizadorAtualizado.getNome() == null || organizadorAtualizado.getNome().trim().isEmpty() ||
                organizadorAtualizado.getSenha() == null || organizadorAtualizado.getSenha().trim().isEmpty()) {

            return ResponseEntity.status(400, "Erro: Os dados atualizados não podem estar vazios.");
        }

        // Atualiza os dados usando o metodo novo
        repositorio.atualizarDadosOrganizador(organizadorExistente, organizadorAtualizado);

        return ResponseEntity.ok("Perfil do organizador atualizado com sucesso!");
    }

    // API 4: VISUALIZAR PERFIL
    @GetMapping(path = "/{organizadorId}")
    public ResponseEntity<?> visualizarPerfilOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId) {
        // Regra de negócio: buscar organizador pelo e-mail
        long idNumerico;

        try {
            idNumerico = Long.parseLong(organizadorId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        // Regra de negócio: se não existir, retornar 404
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado");
        }

        if (organizador.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Organizador possui data de nascimento inválida.");
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
    @PatchMapping(path = "/{organizadorId}/desativar")
    public ResponseEntity<?> desativarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId) {
        // Regra de negócio: buscar organizador pelo e-mail
        long idNumerico;

        try {
            idNumerico = Long.parseLong(organizadorId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        // Regra de negócio: se não existir, retornar 404
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        // Regra de negócio: verificar se já está inativo
        if (!organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador já está inativo!");
        }

        // Regra de negócio : só pode desativar se não tiver eventos ativos ou em execução
        List<Evento> listaEventos = repositorio.listarEventoPorOrganizador(organizadorId); // metodo a ser implementado
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
    @PatchMapping(path = "/{organizadorId}/ativar")
    public ResponseEntity<?> ativarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId,
                                               @RequestBody Organizador.Credenciais credenciais) {
        // Regra de negócio: senha obrigatória para reativação
        if (credenciais.senha() == null || credenciais.senha().isEmpty()) {
            return ResponseEntity.status(400, "Senha é obrigatória.");
        }

        // Regra de negócio: buscar organizador pelo e-mail
        long idNumerico;

        try {
            idNumerico = Long.parseLong(organizadorId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        // Regra de negócio: validar a senha fornecida
        if (organizador.getSenha() == null || !organizador.getSenha().equals(credenciais.senha())) {
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

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<String> cadastrarEvento(@PathVariable(parameter = "organizadorId") String organizadorId, @RequestBody Evento evento){

        long idNumerico;

        try {
            idNumerico = Long.parseLong(organizadorId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador não encontrado!");
        }

        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataInicio = evento.getDataInicio();
        LocalDateTime dataFim = evento.getDataFim();

        long duracao = Duration.between(dataInicio,dataFim).toMinutes();

        if(dataInicio.isBefore(hoje)){
            return ResponseEntity.status(422,"A Data de inicio do Evento não é válida!");
        }

        if (dataFim.isBefore(hoje)) {
            return ResponseEntity.status(422, "A data de fim não pode ser anterior à data atual.");
        }

        if (dataFim.isBefore(dataInicio)) {
            return ResponseEntity.status(422, "A data de fim não pode ser anterior à data de início.");
        }


        if(duracao < 30){
            return ResponseEntity.status(422,"Os eventos devem ter no mínimo 30 minutos de duração!");
        }

        repositorio.salvarEvento(organizador.getId(), evento);

        return ResponseEntity.status(201, "Evento criado com sucesso!");
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(@PathVariable(parameter = "organizadorId") String organizadorId , @PathVariable(parameter = "eventoId") String eventoId, @RequestBody Evento evento){
        long idNumericoOrganizador;
        long idNumericoEvento;

        try {
            idNumericoOrganizador = Long.parseLong(organizadorId);
            idNumericoEvento = Long.parseLong(eventoId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataInicio = evento.getDataInicio();
        LocalDateTime dataFim = evento.getDataFim();

        long duracao = Duration.between(dataInicio,dataFim).toMinutes();

        if(dataInicio.isBefore(hoje)){
            return ResponseEntity.status(422,"A Data de inicio do Evento não é válida!");
        }

        if (dataFim.isBefore(hoje)) {
            return ResponseEntity.status(422, "A data de fim não pode ser anterior à data atual.");
        }

        if (dataFim.isBefore(dataInicio)) {
            return ResponseEntity.status(422, "A data de fim não pode ser anterior à data de início.");
        }

        if(duracao < 30){
            return ResponseEntity.status(422,"Os eventos devem ter no mínimo 30 minutos de duração!");
        }

        Evento eventoExistente = repositorio.listarEventoPorOrganizador(idNumericoOrganizador)
                .stream()
                .filter(e -> e.getId() == idNumericoEvento)
                .findFirst()
                .orElse(null);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento não existe!");
        }

        if (!eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está desativado!");
        }


        if ("desativar".equalsIgnoreCase(status)){
            repositorio.desativarEvento(eventoId, organizadorId);
            return ResponseEntity.status(200,"Evento desativado!");
        }

        return ResponseEntity.status(404, "O " + status + " não foi encontrado!");

    }



    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEvento (@PathVariable(parameter = "organizadorId")long organizadorId){

        List<Evento> listadeEventos = repositorio.listarEventoOrganizador(organizadorId);

        if (listadeEventos.isEmpty()){

            return ResponseEntity.status(200,"nao ha Eventos");
        }

       return ResponseEntity.ok(listadeEventos);

    }
}
        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está ativo!");
        }

        repositorio.atualizarEvento(idNumericoOrganizador, evento, idNumericoEvento);

        return ResponseEntity.status(200,"Evento Atualizado com sucesso!");
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/ativar")
    public ResponseEntity<String> ativarEvento(@PathVariable(parameter = "organizadorId") String organizadorId, @PathVariable(parameter = "eventoId") String eventoId){

        long idNumericoOrganizador;
        long idNumericoEvento;

        try {
            idNumericoOrganizador = Long.parseLong(organizadorId);
            idNumericoEvento = Long.parseLong(eventoId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        Evento eventoExistente = repositorio.listarEventoPorOrganizador(idNumericoOrganizador)
                .stream()
                .filter(e -> e.getId() == idNumericoEvento)
                .findFirst()
                .orElse(null);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento não existe!");
        }

        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está ativo!");
        }

        if (eventoExistente.getDataInicio().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(422, "Não é possível ativar evento com início anterior à data atual.");
        }

        repositorio.ativarEvento(idNumericoEvento, idNumericoOrganizador);
        return ResponseEntity.status(202,"Evento Ativado!");

    }
}
