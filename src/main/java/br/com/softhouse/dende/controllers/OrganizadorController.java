package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.evento.AtualizarEventoDto;
import br.com.softhouse.dende.dto.evento.CadastrarEventoDto;
import br.com.softhouse.dende.dto.evento.EventosOrganizadorDto;
import br.com.softhouse.dende.dto.evento.StatusEventoDto;
import br.com.softhouse.dende.dto.organizador.AtualizarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.CadastrarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.StatusOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.VisualizarOrganizadorDto;
import br.com.softhouse.dende.mapper.EventoMapper;
import br.com.softhouse.dende.mapper.OrganizadorMapper;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<?> cadastroOrganizador(@RequestBody CadastrarOrganizadorDto dto) {
        //uso do DTO de requisição
        Organizador organizador = OrganizadorMapper.toModel(dto);

        //validação de ausencia de dados
        if (organizador == null) {
            return ResponseEntity.status(400, "Dados do organizador invalidos.");
        }

        //validação de valores validos nos campos
        if (organizador.getNome() == null || organizador.getNome().isBlank() ||
                organizador.getEmail() == null || organizador.getEmail().isBlank() ||
                organizador.getSenha() == null || organizador.getSenha().isBlank() ||
                organizador.getSexo() == null || organizador.getSexo().isBlank()) {

            return ResponseEntity.status(400,
                    "Erro: Os campos obrigatorios do organizador nao podem estar vazios.");
        }

        //validação de data
        if (organizador.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Erro: Data de nascimento e obrigatoria.");
        }

        //Validação temporal simples
        if (organizador.getDataNascimento().isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento invalida.");
        }

        //Validação na formatação do email.
        if (!organizador.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.status(400, "Email inválido.");
        }

        //Validação de unicidade.
        if (repositorio.emailExiste(organizador.getEmail())) {
            return ResponseEntity.status(409,
                    "Erro de Conflito: Ja existe um organizador registado com este e-mail!");
        }

        //Validação dos campos da empresa (se houver)
        if (organizador.getEmpresa() != null) {
            Empresa emp = organizador.getEmpresa();

            String cnpj = emp.getCnpj();
            String razao = emp.getRazaoSocial();
            String fantasia = emp.getNomeFantasia();

            if (cnpj == null || cnpj.isBlank() ||
                    razao == null || razao.isBlank() ||
                    fantasia == null || fantasia.isBlank()) {

                return ResponseEntity.status(400,
                        "Voce informou uma empresa, portanto todos os campos sao obrigatorios.");
            }

            if (cnpj.length() != 14) {
                return ResponseEntity.status(400, "CNPJ inválido.");
            }
        }

        //chamada do metodo no repositorio
        repositorio.salvarOrganizador(organizador);

       //Uso do DTO para a resposta
        StatusOrganizadorDto response = OrganizadorMapper.toStatusDto(
                "Organizador " + organizador.getNome() + " registado com sucesso!",
                organizador
        );

        //retorno ao usuario do estado da ação
        return ResponseEntity.status(201, response);
    }

    @PutMapping(path = "/{organizadorId}")
    public ResponseEntity<?> atualizarOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody AtualizarOrganizadorDto dto
            //@RequestBody Organizador.Credenciais credencial
            ) {

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        //conversão de Identificador de caractere para numerico
        Long idNumerico = parseId(organizadorId);

        //validação de ausencia no campo
        if (idNumerico == null) {
            return ResponseEntity.status(400, "Erro: ID invalido.");
        }

        //Busca do objeto na persistencia
        Organizador organizadorExistente = repositorio.buscarOrganizadorPorId(idNumerico);

        //Validação de objeto presente
        if (organizadorExistente == null) {
            return ResponseEntity.status(404, "Erro: Organizador nao encontrado com este ID.");
        }

        /*if (!credencial.email().equals(organizadorExistente.getEmail())
                ||!credencial.senha().equals(organizadorExistente.getSenha())) {
            return ResponseEntity.status(401, "Credenciais de acesso invalidas.");
        }*/

        //Conversão do DTO de requisição para um objeto.
        Organizador organizadorAtualizado = OrganizadorMapper.toModel(dto);

        //validação de objeto de requisição presente
        if (organizadorAtualizado == null) {
            return ResponseEntity.status(400, "Dados do organizador invalidos.");
        }

        //O bloco abaixo valida os valores dos atributos do objeto de requisição
        String nome = organizadorAtualizado.getNome();
        if (nome != null && nome.isBlank()) {
            return ResponseEntity.status(400, "Nome inválido.");
        }

        String senha = organizadorAtualizado.getSenha();
        if (senha != null && senha.isBlank()) {
            return ResponseEntity.status(400, "Senha inválida.");
        }

        String sexo = organizadorAtualizado.getSexo();
        if (sexo != null && sexo.isBlank()) {
            return ResponseEntity.status(400, "Sexo inválido.");
        }

        LocalDate dataNascimento = organizadorAtualizado.getDataNascimento();
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            return ResponseEntity.status(400, "Data de nascimento invalida");
        }

        //Validação dos atributos da empresa (se houver) do objeto de requisição
        if (organizadorAtualizado.getEmpresa() != null) {
            Empresa emp = organizadorAtualizado.getEmpresa();

            String cnpj = emp.getCnpj();
            String razao = emp.getRazaoSocial();
            String fantasia = emp.getNomeFantasia();

            if (cnpj == null || cnpj.isBlank() ||
                    razao == null || razao.isBlank() ||
                    fantasia == null || fantasia.isBlank()) {

                return ResponseEntity.status(400,
                        "Se a empresa for informada, todos os campos sao obrigatorios.");
            }

            if (cnpj.length() != 14) {
                return ResponseEntity.status(400, "CNPJ inválido.");
            }
        }

        //chamada do metodo no repositorio (persistencia)
        repositorio.atualizarDadosOrganizador(organizadorExistente, organizadorAtualizado);

        //Uso de DTO para resposta
        StatusOrganizadorDto resposta = OrganizadorMapper.toStatusDto(
                "Perfil do organizador atualizado com sucesso!",
                organizadorExistente
        );

        //Retorno ao cliente para ação realizada
        return ResponseEntity.status(200, resposta);
    }

    @GetMapping(path = "/{organizadorId}")
    public ResponseEntity<?> visualizarPerfilOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId
                                                         //@RequestBody Organizador.Credenciais credencial
                                                         ) {

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado");
        }

        /*if (!credencial.email().equals(organizador.getEmail())
                ||!credencial.senha().equals(organizador.getSenha())) {
            return ResponseEntity.status(401, "Credenciais de acesso invalidas.");
        }*/

        if (organizador.getDataNascimento() == null) {
            return ResponseEntity.status(400, "Organizador possui data de nascimento invalida.");
        }

        VisualizarOrganizadorDto resposta = OrganizadorMapper.toVisualizarDto(organizador);
        return ResponseEntity.status(200,resposta);
    }

    @PatchMapping(path = "/{organizadorId}/desativar")
    public ResponseEntity<?> desativarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId
                                                  //@RequestBody Organizador.Credenciais credencial
                                                  ) {

       /* if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado!");
        }

        /*if (!credencial.email().equals(organizador.getEmail())
            ||!credencial.senha().equals(organizador.getSenha())) {
            return ResponseEntity.status(401, "Senha invalida.");
        }*/

        if (!organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador ja esta inativo!");
        }

        List<Evento> listaEventos = repositorio.listarEventoPorOrganizador(idNumerico);
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
            return ResponseEntity.status(409, "Nao e possivel desativar: este organizador possui eventos ativos.");
        }

        organizador.setAtivo(false);
        repositorio.salvarOrganizador(organizador);
        StatusOrganizadorDto resposta = OrganizadorMapper.toStatusDto(
                "Organizador desativado com sucesso!",
                organizador
        );
        return ResponseEntity.status(200, resposta);
    }

    @PatchMapping(path = "/{organizadorId}/ativar")
    public ResponseEntity<?> ativarOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId
                                               //@RequestBody Organizador.Credenciais credencial
                                               ){

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);

        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado!");
        }

        /*if (!credencial.email().equals(organizador.getEmail())
                ||!credencial.senha().equals(organizador.getSenha())) {
            return ResponseEntity.status(401, "Senha invalida.");
        }*/

        if (organizador.isAtivo()) {
            return ResponseEntity.status(409, "Organizador ja esta ativo!");
        }

        organizador.setAtivo(true);
        repositorio.salvarOrganizador(organizador);
        StatusOrganizadorDto resposta = OrganizadorMapper.toStatusDto(
                "Organizador reativado com sucesso!",
                organizador
        );
        return ResponseEntity.status(200, resposta);
    }

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> cadastrarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                             @RequestBody CadastrarEventoDto dto) {
        Long idNumerico = parseId(organizadorId);
        if (idNumerico == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumerico);
        if (organizador == null) {
            return ResponseEntity.status(404, "Organizador nao encontrado!");
        }

        if (    dto.nome() == null || dto.nome().isBlank() ||
                dto.localEvento() == null || dto.localEvento().isBlank() ||
                dto.capacidadeMaxima() == null || dto.capacidadeMaxima() <= 0 ||
                dto.precoUnitarioIngresso() == null || dto.precoUnitarioIngresso() < 0 ){
            return ResponseEntity.status(400, "Os campos minimos para um evento" +
                    " estão ausentes, em branco ou invalidos");
        }

        Evento evento = EventoMapper.toModel(dto);

        if (dto.eventoPrincipalId() != null) {
            Evento principal = repositorio.buscarEvento(dto.eventoPrincipalId());

            if (principal == null) {
                return ResponseEntity.status(404, "Evento principal não encontrado");
            }

            evento.setEventoPrincipal(principal);
        }

        ResponseEntity<?> validacao = validarDatasEvento(evento);
        if (validacao != null) {
            return validacao;
        }

        if (evento.isEventoAtivo()) {
            return ResponseEntity.status(409, "Nao se pode criar um evento ativo.");
        }

        evento.setOrganizador(organizador);
        repositorio.salvarEvento(idNumerico, evento);

        StatusEventoDto resposta = EventoMapper.toStatusEventoDto("Evento criado com sucesso!", evento);
        return ResponseEntity.status(201, resposta);
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<?> alterarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                           @PathVariable(parameter = "eventoId") String eventoId,
                                           @RequestBody AtualizarEventoDto dto) {
        Long idNumericoOrganizador = parseId(organizadorId);
        Long idNumericoEvento = parseId(eventoId);

        if (idNumericoOrganizador == null || idNumericoEvento == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }
        Evento eventoExistente = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento nao existe!");
        }

        if (!eventoExistente.isEventoAtivo()){
            return ResponseEntity.status(422, "O evento não pode ser alterado pois se encontra inativo.");
        }

        Evento evento = EventoMapper.toModel(dto);

        if (dto.eventoEstorno() != null){
            evento.setEventoEstorno(dto.eventoEstorno());
        }

        if (dto.precoUnitarioIngresso() != null && dto.precoUnitarioIngresso()  < 0){
            return ResponseEntity.status(400, "O preço do ingresso não pode ser negativo");
        }else if (dto.precoUnitarioIngresso() != null){
            evento.setPrecoUnitarioIngresso(dto.precoUnitarioIngresso());
        }

        if (dto.taxaCancelamento() != null && dto.taxaCancelamento()  < 0){
            return ResponseEntity.status(400, "A taxa de cancelamento não pode ser negativo");
        }else if (dto.taxaCancelamento() != null){
            evento.setTaxaCancelamento(dto.taxaCancelamento());
        }

        if (dto.capacidadeMaxima() != null && dto.capacidadeMaxima()  < 0){
            return ResponseEntity.status(400, "A capacidade maxima não pode ser negativo");
        }else if (dto.capacidadeMaxima() != null){
            evento.setCapacidadeMaxima(dto.capacidadeMaxima());
        }

        if (dto.eventoPrincipalId() != null) {
            Evento principal = repositorio.buscarEvento(dto.eventoPrincipalId());

            if (principal == null) {
                return ResponseEntity.status(404, "Evento principal não encontrado");
            }

            evento.setEventoPrincipal(principal);
        }

        ResponseEntity<?> validacao = validarDatasEvento(evento);
        if (validacao != null) {
            return validacao;
        }

        repositorio.atualizarEvento(idNumericoOrganizador, evento, idNumericoEvento);

        Evento eventoAtualizado = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);
        StatusEventoDto resposta = EventoMapper.toStatusEventoDto("Evento atualizado com sucesso!", eventoAtualizado);
        return ResponseEntity.status(200, resposta);
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/ativar")
    public ResponseEntity<?> ativarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                          @PathVariable(parameter = "eventoId") String eventoId
                                          //@RequestBody Organizador.Credenciais credencial
                                          ) {
        Long idNumericoOrganizador = parseId(organizadorId);
        Long idNumericoEvento = parseId(eventoId);

        if (idNumericoOrganizador == null || idNumericoEvento == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        Evento eventoExistente = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);
        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento nao existe!");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumericoOrganizador);

        /*if (!credencial.email().equals(organizador.getEmail())
                ||!credencial.senha().equals(organizador.getSenha())) {
            return ResponseEntity.status(401, "Senha invalida.");
        }*/

        if (eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento ja esta ativo!");
        }

        ResponseEntity<?> validacao = validarDatasEvento(eventoExistente);
        if (validacao != null) {
            return validacao;
        }

        repositorio.ativarEvento(idNumericoEvento, idNumericoOrganizador);
        StatusEventoDto resposta = EventoMapper.toStatusEventoDto("Evento ativado!", eventoExistente);
        return ResponseEntity.status(200, resposta);
    }

    @PatchMapping(path = "/{organizadorId}/eventos/{eventoId}/desativar")
    public ResponseEntity<?> desativarEvento(@PathVariable(parameter = "organizadorId") String organizadorId,
                                             @PathVariable(parameter = "eventoId") String eventoId
                                             //@RequestBody Organizador.Credenciais credencial
                                             ) {
        Long idNumericoOrganizador = parseId(organizadorId);
        Long idNumericoEvento = parseId(eventoId);

        if (idNumericoOrganizador == null || idNumericoEvento == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        /*if (    credencial.email() == null || credencial.email().isEmpty() ||
                credencial.senha() == null || credencial.senha().isEmpty()) {
            return ResponseEntity.status(400, "Email ou Senha ausentes.");
        }*/

        Evento eventoExistente = buscarEventoDoOrganizador(idNumericoOrganizador, idNumericoEvento);

        if (eventoExistente == null) {
            return ResponseEntity.status(404, "O Evento nao existe.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumericoOrganizador);

        /*if (!credencial.email().equals(organizador.getEmail())
                ||!credencial.senha().equals(organizador.getSenha())) {
            return ResponseEntity.status(401, "Senha invalida.");
        }*/

        if (!eventoExistente.isEventoAtivo()) {
            return ResponseEntity.status(422, "O Evento ja esta desativado!");
        }

        repositorio.desativarEvento(idNumericoEvento, idNumericoOrganizador);
        StatusEventoDto resposta = EventoMapper.toStatusEventoDto("Evento desativado!", eventoExistente);
        return ResponseEntity.status(200, resposta);
    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarEventosDoOrganizador(@PathVariable(parameter = "organizadorId") String organizadorId) {
        Long idNumericoOrganizador = parseId(organizadorId);
        if (idNumericoOrganizador == null) {
            return ResponseEntity.status(400, "ID invalido.");
        }

        Organizador organizador = repositorio.buscarOrganizadorPorId(idNumericoOrganizador);

        if (organizador == null){
            return ResponseEntity.status(404, "Organizador não encontrado.");
        }

        List<EventosOrganizadorDto> listaEventos = repositorio.listarEventoPorOrganizador(idNumericoOrganizador)
                .stream()
                .map(EventoMapper::toEventosOrganizadorDto)
                .toList();

        if (listaEventos.isEmpty()) {
            return ResponseEntity.status(200, "Vocẽ não possui eventos cadastradoes.");
        }

        return ResponseEntity.status(200,listaEventos);
    }

    private Long parseId(String valor) {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Evento buscarEventoDoOrganizador(Long organizadorId, Long eventoId) {
        return repositorio.listarEventoPorOrganizador(organizadorId)
                .stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .orElse(null);
    }

    private ResponseEntity<?> validarDatasEvento(Evento evento) {
        if (evento == null || evento.getDataInicio() == null || evento.getDataFim() == null) {
            return ResponseEntity.status(400, "Datas do evento sao obrigatorias.");
        }

        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataInicio = evento.getDataInicio();
        LocalDateTime dataFim = evento.getDataFim();

        if (dataInicio.isBefore(hoje)) {
            return ResponseEntity.status(422, "A data de inicio do evento nao e valida!");
        }

        if (dataFim.isBefore(hoje)) {
            return ResponseEntity.status(422, "A data de fim nao pode ser anterior a data atual.");
        }

        if (dataFim.isBefore(dataInicio)) {
            return ResponseEntity.status(422, "A data de fim nao pode ser anterior a data de inicio.");
        }

        long duracao = Duration.between(dataInicio, dataFim).toMinutes();
        if (duracao < 30) {
            return ResponseEntity.status(422, "Os eventos devem ter no minimo 30 minutos de duracao!");
        }

        return null;
    }
}
