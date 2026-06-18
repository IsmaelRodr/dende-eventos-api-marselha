package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Usuario;
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

    // API 02 - Cadastrar Utilizador Organizador
    // [AVALIAÇÃO - Item 1] O nome do método 'cadastroOrganizador()' usa um substantivo, quando métodos
    // em Java devem ser verbos de ação. Sugestão: 'cadastrarOrganizador()'
    // A nova assinatura ficaria: public ResponseEntity<String> cadastrarOrganizador(@RequestBody Organizador organizador)
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
        List<Evento> listaEventos = repositorio.listarEventoPorOrganizador(idNumerico); // metodo a ser implementado
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
        // [AVALIAÇÃO - Item 7] Retornar 409 quando o organizador já está inativo fere a idempotência
        // do endpoint PATCH /desativar. Uma operação idempotente deveria retornar 200/204 mesmo se o
        // recurso já estiver no estado desejado (organizador já desativado).
        // [AVALIAÇÃO - Item 9] O status 200 é aceitável. Para operações de atualização de estado sem
        // retorno de corpo significativo, 204 No Content é também uma opção semântica válida.
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

        if (evento.isEventoAtivo()){
            return ResponseEntity.status(409,"Nao se pode Criar um evento ativo.");
        }

        evento.setOrganizador(idNumerico);
        repositorio.salvarEvento(idNumerico, evento);

        return ResponseEntity.status(201, "Evento criado com sucesso!");
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(@PathVariable(parameter = "organizadorId") String organizadorId , @PathVariable(parameter = "eventoId") String eventoId, @RequestBody Evento evento){
        // [AVALIAÇÃO - Item 6] As validações de datas e duração mínima abaixo estão duplicadas:
        // existem tanto aqui no Controller quanto dentro do método 'atualizarEvento()' do Repositório.
        // Ter a mesma regra de negócio em dois lugares diferentes gera inconsistência de manutenção
        // (se a regra mudar, precisa ser alterada em ambos os locais).
        // Sugestão: centralize as validações em um único lugar — preferencialmente no Controller ou
        // em uma classe EventoService, e remova-as do Repositório.
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

        // [AVALIAÇÃO - Bug crítico - US 8] A US 8 diz: "eu quero alterar um evento ATIVO que eu cadastrei".
        // O código abaixo faz o oposto: impede a alteração de eventos ativos, retornando 422.
        // Esta verificação inverte completamente a regra de negócio solicitada.
        // Sugestão: remova este bloco (ou inverta a condição se a intenção era impedir alteração de inativos)
        // e permita a alteração de eventos ativos, bloqueando apenas a mudança do campo 'eventoAtivo'
        // (que é gerenciado pelos endpoints /ativar e /desativar).
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

        // [AVALIAÇÃO - Item 7] Retornar 422 quando o evento já está ativo fere a idempotência do endpoint
        // PATCH /ativar. O resultado de "ativar um evento já ativo" é o mesmo: evento ativo.
        // Sugestão: retorne 200 com mensagem informativa ao invés de um erro, preservando a idempotência.
        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está ativo!");
        }
        // faltou o else
        else {
            return ResponseEntity.status(422, "Não é possível ativar evento com início anterior à data atual.");
        }

        repositorio.ativarEvento(idNumericoEvento, idNumericoOrganizador);
        return ResponseEntity.status(200,"Evento Ativado!");

    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/desativar")
    public ResponseEntity<String> desativarEvento(@PathVariable(parameter = "organizadorId") String organizadorId, @PathVariable(parameter = "eventoId") String eventoId){

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

        if (eventoExistente == null){
            return ResponseEntity.status(404, "O Evento não existe.");
        }

        if (!eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento já está desativado!");
        }

        repositorio.desativarEvento(idNumericoEvento, idNumericoOrganizador);
        return ResponseEntity.status(200,"Evento desativado!");
    }

    // [AVALIAÇÃO - Item 1] O nome do método 'listarEvento()' está no singular, o que não reflete
    // corretamente o que o método faz (retorna uma lista de eventos). Métodos que retornam coleções
    // devem ter seus nomes no plural para clareza semântica.
    // Sugestão: renomeie para 'listarEventos()'.
    // A nova assinatura ficaria: public ResponseEntity<?> listarEventos(@PathVariable...)
    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEvento (@PathVariable(parameter = "organizadorId")String organizadorId){

        long idNumericoOrganizador;

        try {
            idNumericoOrganizador = Long.parseLong(organizadorId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido.");
        }

        List<Map<String, Object>> listaEventos = repositorio.listarEventoPorOrganizador(idNumericoOrganizador)
                .stream().map(e -> { Map<String, Object> eventoMap = new HashMap<>();
                    eventoMap.put("nome", e.getNome());
                    eventoMap.put("dataInicio", e.getDataInicio());
                    eventoMap.put("dataFim", e.getDataFim());
                    eventoMap.put("local", e.getLocalEvento());
                    eventoMap.put("precoIngresso", e.getPrecoUnitarioIngresso());
                    eventoMap.put("capacidadeMaxima", e.getCapacidadeMaxima());
                    return eventoMap; })
                .toList();

        // [AVALIAÇÃO - Item 9] O status 204 (No Content) indica que a resposta não possui corpo.
        // Retornar 204 com a string "nao ha Eventos" é semanticamente incorreto, pois 204 pressupõe
        // ausência de corpo na resposta. O correto seria retornar 200 com uma lista vazia ou
        // 404 com uma mensagem descritiva.
        // Sugestão: return ResponseEntity.status(200, Collections.emptyList());
        // [AVALIAÇÃO - Funcionalidade] A US 11 exige que a listagem de eventos do organizador esteja
        // ordenada por data de execução e ordem alfabética de nome. A lista retornada aqui não aplica
        // nenhuma ordenação.
        // Sugestão: adicione .sorted(Comparator.comparing(e -> e.getDataInicio()).thenComparing(e -> e.getNome()))
        // antes do .map() no stream.
        if (listaEventos.isEmpty()){
            return ResponseEntity.status(204,"nao ha Eventos");
        }

        return ResponseEntity.ok(listaEventos);
    }

    // [AVALIAÇÃO - Mapeamento incorreto] A US 13 ("Comprar Ingresso") descreve uma ação do
    // USUÁRIO COMUM, não do organizador. Este endpoint está mapeado em OrganizadorController,
    // quando deveria estar em UsuarioController. Além disso, o caminho
    // POST /organizadores/{organizadorId}/eventos/{eventoId}/ingressos expõe o organizadorId
    // como parâmetro de rota, o que não é natural para a ação de um usuário comprando um ingresso.
    // Sugestão: mova este endpoint para UsuarioController com o caminho:
    // POST /usuarios/{usuarioId}/ingressos  (recebendo o eventoId no corpo da requisição)
    // [AVALIAÇÃO - Item 4] A resposta de compra retorna apenas ingressoId, evento e valorTotal.
    // O ingresso do evento principal (caso exista) não é incluído na resposta. Conforme a US 13,
    // dois ingressos devem ser gerados — ambos deveriam constar na resposta para o usuário.
    @PostMapping(path = "/{organizadorId}/eventos/{eventoId}/ingressos")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "organizadorId") String organizadorIdString,
            @PathVariable(parameter = "eventoId") String eventoIdString,
            @RequestBody Map<String, Long> request) {

        // o body recebe o usuarioId
        try {
            long organizadorId = Long.parseLong(organizadorIdString);
            long eventoId = Long.parseLong(eventoIdString);

            if (request == null || request.get("usuarioId") == null) {
                return ResponseEntity.status(400, "Body inválido: usuarioId obrigatório");
            }
            long usuarioId = ((Number) request.get("usuarioId")).longValue();

            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            if (usuario == null || !usuario.isAtivo()) {
                return ResponseEntity.status(404, "Usuário não encontrado ou inativo");
            }

            List<Evento> eventosOrganizador = repositorio.listarEventoPorOrganizador(organizadorId);
            Evento evento = eventosOrganizador.stream().filter(e -> e.getId().equals(eventoId)).findFirst().orElse(null);
            if (evento == null) {
                return ResponseEntity.status(404, "Evento não encontrado");
            }
            if (!evento.isEventoAtivo()){
                return ResponseEntity.status(422, "Evento inativo");
            }
            if (evento.getIngressosDisponiveis() <= 0){
                return ResponseEntity.status(409, "Vagas esgotadas");
            }
            if (evento.getDataInicio().isBefore(LocalDateTime.now())){
                return ResponseEntity.status(422, "Evento expirado");
            }

            Map<String, Object> resultado = repositorio.comprarIngresso(usuarioId, eventoId);
            if (resultado == null){
                return ResponseEntity.status(409, "Falha na compra");
            }

            Ingresso ingresso = (Ingresso) resultado.get("ingresso");
            double valorTotal = (double) resultado.get("valorTotal");

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("ingressoId", ingresso.getId());
            resposta.put("evento", evento.getNome());
            resposta.put("valorTotal", valorTotal);
            return ResponseEntity.status(201, resposta);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(400, "ID inválido");
        }
    }
}