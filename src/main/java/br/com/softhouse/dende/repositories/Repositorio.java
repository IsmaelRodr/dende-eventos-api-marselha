package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class Repositorio {

    private static final Repositorio instance = new Repositorio();
    private final Map<Long, Usuario> usuariosComum;
    private final Map<Long, Organizador> organizadores;
    private final Map<Long, List<Evento>> eventos;
    private final Map<Long, List<Ingresso>> ingressosPorUsuario;

    // [AVALIAÇÃO - Item 13] Ótimo: cada entidade possui o seu próprio contador independente.
    // Isso é correto, pois cada entidade cresce de forma independente e os IDs não devem ser
    // compartilhados entre tipos diferentes (ex: um Usuário com id=1 e um Evento com id=1 são
    // objetos distintos e não podem depender do mesmo gerador).
    // [AVALIAÇÃO - Item 11] Os contadores geram valores sequenciais iniciando em 1 e incrementando
    // a cada novo objeto salvo (contadorX++). Isso está correto e garante unicidade dos IDs em memória.
    private Long contadorUsuarios = 1L;
    private Long contadorOrganizadores = 1L;
    private Long contadorEventos = 1L;
    private Long contadorIngressos = 1L;

    // 2. Construtor privado para o Singleton
    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
        this.ingressosPorUsuario = new HashMap<>();
    }

    // 3. O famigerado metodo getInstance() que estava a dar erro!
    public static Repositorio getInstance() {
        return instance;
    }

    public void salvarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(contadorUsuarios++);
        }
        usuariosComum.put(usuario.getId(), usuario);
    }

    public void salvarOrganizador(Organizador organizador) {
        if (organizador.getId() == null) {
            organizador.setId(contadorOrganizadores++);
        }
        organizadores.put(organizador.getId(), organizador);
    }

    // [AVALIAÇÃO - Item 8] O método 'buscarUsuarioPorId()' retorna null quando o usuário não é encontrado.
    // Retornar null obriga o código chamador a verificar nulidade manualmente, aumentando o risco de
    // NullPointerException se o retorno não for checado.
    // Sugestão: utilize Optional<Usuario> como tipo de retorno para tornar explícita a possibilidade de ausência.
    // A nova assinatura ficaria: public Optional<Usuario> buscarUsuarioPorId(Long id)
    // Retorno: return Optional.ofNullable(usuariosComum.get(id));
    public Usuario buscarUsuarioPorId(Long id) {
        return usuariosComum.get(id);
    }

    // [AVALIAÇÃO - Item 8] Mesma observação do 'buscarUsuarioPorId': retornar null quando o
    // organizador não é encontrado força verificações manuais no código chamador.
    // Sugestão: utilize Optional<Organizador> como tipo de retorno.
    // A nova assinatura ficaria: public Optional<Organizador> buscarOrganizadorPorId(Long id)
    // Retorno: return Optional.ofNullable(organizadores.get(id));
    public Organizador buscarOrganizadorPorId(Long id) {
        return organizadores.get(id);
    }

    public boolean emailExiste(String email) {
        // [AVALIAÇÃO - Item 3] A verificação 'email != null' pode ser substituída por chamadas mais expressivas
        // da API Objects do Java, tornando o código mais idiomático e legível.
        // Sugestão 1: Objects.nonNull(email) – apenas verifica se não é nulo, sem lançar exceção. Adequado aqui,
        //             pois um email nulo simplesmente significa "sem conflito".
        // Sugestão 2: Objects.requireNonNull(email, "Email não pode ser nulo") – verifica e lança
        //             NullPointerException com mensagem se o valor for nulo. Adequado quando nulo é um erro.
        // Aplicando a sugestão 1, a linha ficaria:
        // if (Objects.nonNull(email) && email.equals(u.getEmail())) return true;
        for (Usuario u : usuariosComum.values()) {
            if (email != null && email.equals(u.getEmail())) return true;
        }
        for (Organizador o : organizadores.values()) {
            if (email != null && email.equals(o.getEmail())) return true;
        }
        return false;
    }

    // --- MÉTODOS DE ATUALIZAR COMPLETOS (Pedido do Líder) ---

    public void atualizarDadosUsuario(Usuario usuarioExistente, Usuario novosDados) {
        // [AVALIAÇÃO - Item 3] Os múltiplos blocos 'if (campo != null)' abaixo podem ser substituídos pela
        // notação mais expressiva de Objects.nonNull(). Exemplo:
        // if (Objects.nonNull(novosDados.getNome())) usuarioExistente.setNome(novosDados.getNome());
        if (novosDados.getNome() != null)
            usuarioExistente.setNome(novosDados.getNome());

        if (novosDados.getDataNascimento() != null)
            usuarioExistente.setDataNascimento(novosDados.getDataNascimento());

        if (novosDados.getSexo() != null)
            usuarioExistente.setSexo(novosDados.getSexo());

        if (novosDados.getSenha() != null)
            usuarioExistente.setSenha(novosDados.getSenha());

        usuariosComum.put(usuarioExistente.getId(), usuarioExistente);
    }

    public void atualizarDadosOrganizador(Organizador orgExistente, Organizador novosDados) {
        if (novosDados.getNome() != null)
            orgExistente.setNome(novosDados.getNome());

        if (novosDados.getDataNascimento() != null)
            orgExistente.setDataNascimento(novosDados.getDataNascimento());

        if (novosDados.getSexo() != null)
            orgExistente.setSexo(novosDados.getSexo());

        if (novosDados.getSenha() != null)
            orgExistente.setSenha(novosDados.getSenha());

        orgExistente.setEmpresa(novosDados.getEmpresa());  // Nova classe Empresa

        organizadores.put(orgExistente.getId(), orgExistente);
    }

    public void salvarEvento(Long organizadorId, Evento evento) {
        if (evento.getId() == null || evento.getId() == 0) {
            evento.setId(contadorEventos++);
        }
        eventos.computeIfAbsent(organizadorId, o -> new ArrayList<>())
                .add(evento);
    }

    public void atualizarEvento(long organizadorId , Evento evento, long eventoId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // [AVALIAÇÃO - Bug] O operador '==' compara referências de objetos Long, não seus valores.
        // Para IDs maiores que 127, o cache do Java não cobre e a comparação pode retornar false
        // mesmo com valores numericamente iguais.
        // Sugestão: utilize .equals() ou converta explicitamente: e.getId().equals(eventoId)
        // Como 'eventoId' aqui é um 'long' primitivo, o autoboxing pode mascarar o problema em alguns
        // casos, mas o uso de .equals() é mais seguro e explícito.
        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        // [AVALIAÇÃO - Item 6] As validações de datas e duração mínima abaixo são regras de negócio e não
        // deveriam estar no Repositório. A responsabilidade do Repositório é persistir e recuperar dados.
        // Regras de negócio pertencem a uma camada de Serviço ou ao Controller.
        // Sugestão: mova essas verificações para o Controller (já existem em cadastrarEvento) ou crie
        // uma classe EventoService que centralize todas as validações de negócio do Evento.
        if (evento.getDataInicio() != null && evento.getDataFim() != null) {
            if (evento.getDataFim().isBefore(evento.getDataInicio())) {
                throw new IllegalArgumentException("Data fim não pode ser anterior à data início.");
            } if (Duration.between(evento.getDataInicio(), evento.getDataFim()).toMinutes() < 30) {
                throw new IllegalArgumentException("Evento deve ter no mínimo 30 minutos.");
            }
        }

        eventoExistente.setNome(evento.getNome());
        eventoExistente.setDescricao(evento.getDescricao());
        eventoExistente.setPaginaWeb(evento.getPaginaWeb());
        eventoExistente.setDataInicio(evento.getDataInicio());
        eventoExistente.setDataFim(evento.getDataFim());
        eventoExistente.setTipoEvento(evento.getTipoEvento());
        eventoExistente.setEventoPrincipal(evento.getEventoPrincipal());
        eventoExistente.setModalidade(evento.getModalidade());
        eventoExistente.setPrecoUnitarioIngresso(evento.getPrecoUnitarioIngresso());
        eventoExistente.setTaxaCancelamento(evento.getTaxaCancelamento());
        eventoExistente.setEventoEstorno(evento.isEventoEstorno());
        eventoExistente.setCapacidadeMaxima(evento.getCapacidadeMaxima());
        eventoExistente.setLocalEvento(evento.getLocalEvento());

    }

    public void ativarEvento(long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // [AVALIAÇÃO - Bug] Mesmo problema de comparação de Long com '==' descrito acima.
        // Sugestão: e.getId().equals(eventoId) ou Objects.equals(e.getId(), eventoId)
        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        eventoExistente.setEventoAtivo(true);
        liberarIngressosEvento(eventoId, organizadorId);
    }

    public void desativarEvento(long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        cancelarTodosIngressosEvento(eventoId);
        eventoExistente.setEventoAtivo(false);
        eventoExistente.setIngressosDisponiveis(0);
    }

    public List<Evento> listarEventoPorOrganizador(Long  organizadorId) {

        return eventos.getOrDefault(organizadorId, Collections.emptyList());

    }


    public List<Evento> listarEventoAtivos(){
        List<Evento> eventosAtivos = new ArrayList<>();
        for ( List<Evento> eventoGerais: eventos.values()){
            for(Evento evento: eventoGerais){
                // [AVALIAÇÃO - Bug] A condição 'evento.getCapacidadeMaxima() > 0' verifica a capacidade
                // total cadastrada, e não os ingressos ainda disponíveis para venda. Um evento com
                // capacidade total de 100 e 0 ingressos restantes passaria incorretamente neste filtro.
                // A US 12 exige que eventos sem ingressos disponíveis não apareçam no feed.
                // Sugestão: substitua por: evento.getIngressosDisponiveis() > 0
                if (evento.isEventoAtivo() && evento.getCapacidadeMaxima()>0){
                    eventosAtivos.add(evento);

                }
            }
        }
        return eventosAtivos;
    }

    private void salvarIngresso(Ingresso ingresso) {
        if (ingresso.getId() == null) {
            ingresso.setId(contadorIngressos++);
        }
        ingressosPorUsuario.computeIfAbsent(ingresso.getUsuario().getId(), k -> new ArrayList<>()).add(ingresso);
    }

    // [AVALIAÇÃO - Item 8] O método 'encontrarEvento()' retorna null quando o evento não é encontrado.
    // Retornar null força o código chamador a verificar nulidade, aumentando o risco de NullPointerException.
    // Em Java moderno, o retorno de null para representar "ausência de valor" é considerado uma prática a evitar.
    // Sugestão: utilize Optional<Evento> como tipo de retorno.
    // A nova assinatura ficaria: private Optional<Evento> encontrarEvento(Long eventoId)
    // Retorno quando não encontrado: return Optional.empty();
    // Retorno quando encontrado: return Optional.of(e);
    private Evento encontrarEvento(Long eventoId) {
        for (List<Evento> eventosOrg : eventos.values()) {
            for (Evento e : eventosOrg) {
                if (e.getId().equals(eventoId)) return e;
            }
        }
        return null;
    }

    //Liberar ingressos do evento (chamado na ativação)
    public void liberarIngressosEvento(Long eventoId, Long organizadorId) {
        List<Evento> lista = eventos.get(organizadorId);
        if (lista != null) {
            lista.stream()
                    .filter(e -> e.getId().equals(eventoId))
                    .findFirst().ifPresent(evento -> evento.disponibilizarIngressos(evento.getCapacidadeMaxima()));
        }
    }

    // [AVALIAÇÃO - Item 8] O método 'comprarIngresso()' retorna um Map<String, Object> genérico.
    // Isso torna o código frágil (chaves do Map são strings sem verificação de tipo em compile-time)
    // e dificulta a manutenção. Sugestão: crie um DTO de retorno (ex: ResultadoCompraDTO com campos
    // 'ingresso' e 'valorTotal') ou utilize um record: record ResultadoCompra(Ingresso ingresso, double valorTotal) {}
    // [AVALIAÇÃO - Item 6] As validações de negócio abaixo (evento ativo, ingressos disponíveis,
    // data de início) são regras de negócio que pertencem ao Controller ou a uma camada de Service,
    // não ao Repositório. O Repositório deve apenas persistir e recuperar dados.
    // Sugestão: mova as validações para o Controller (OrganizadorController.comprarIngresso) ou crie
    // uma classe EventoService.
    public Map<String, Object> comprarIngresso(Long usuarioId, Long eventoId) {
        Usuario usuario = usuariosComum.get(usuarioId);
        if (usuario == null) return null;

        Evento evento = encontrarEvento(eventoId);
        if (evento == null) return null;

        // Validações do evento solicitado
        if (!evento.isEventoAtivo() || evento.getIngressosDisponiveis() <= 0 ||
                evento.getDataInicio().isBefore(LocalDateTime.now())) {
            return null;
        }

        Evento eventoPrincipal = evento.getEventoPrincipal();

        // Se houver evento principal
        if (eventoPrincipal != null) {
            if (!eventoPrincipal.isEventoAtivo() ||
                    eventoPrincipal.getIngressosDisponiveis() <= 0 ||
                    eventoPrincipal.getDataInicio().isBefore(LocalDateTime.now())) {
                return null;
            }
        }

        // Cria ingresso do evento solicitado
        Ingresso ingresso = new Ingresso(null, usuario, evento, evento.getPrecoUnitarioIngresso(), usuario.getEmail());
        salvarIngresso(ingresso);
        evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() - 1);

        double valorTotal = ingresso.getValorPago();

        // Se houver principal
        if (eventoPrincipal != null) {
            Ingresso ingressoPrincipal = new Ingresso(null, usuario, eventoPrincipal,
                    eventoPrincipal.getPrecoUnitarioIngresso(), usuario.getEmail());
            salvarIngresso(ingressoPrincipal);
            eventoPrincipal.setIngressosDisponiveis(eventoPrincipal.getIngressosDisponiveis() - 1);
            valorTotal += ingressoPrincipal.getValorPago();
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("ingresso", ingresso);
        resultado.put("valorTotal", valorTotal);
        return resultado;
    }

    // [AVALIAÇÃO - Item 7] O método 'cancelarIngresso()' lança uma IllegalStateException quando o
    // evento não permite estorno. Isso pode ferir a idempotência: chamar o método novamente com os
    // mesmos parâmetros produz comportamentos diferentes dependendo da política de estorno do evento.
    // Além disso, a US 14 diz "O ingresso deve ser marcado como cancelado, o valor pago estornado
    // conforme as regras do evento". Isso implica que o cancelamento SEMPRE ocorre; o que varia é
    // se o valor é devolvido ou não — não se o cancelamento é permitido.
    // Sugestão: sempre cancele o ingresso; aplique o estorno somente se 'evento.isEventoEstorno()' for true
    // (com valorEstornado = 0 em caso de não estorno).
    public boolean cancelarIngresso(Long usuarioId, Long ingressoId) {
        List<Ingresso> ingressos = ingressosPorUsuario.get(usuarioId);
        if (ingressos == null) return false;

        for (Ingresso ingresso : ingressos) {
            if (ingresso.getId().equals(ingressoId) && !ingresso.isCancelado()) {
                Evento evento = ingresso.getEvento();

                //Verifica se o evento permite estorno
                if (!evento.isEventoEstorno()) {
                    throw new IllegalStateException("Este evento não permite cancelamento com estorno.");
                }

                //Calcula valor estornado aplicando a taxa de cancelamento
                double taxa = evento.getTaxaCancelamento(); // considerando percentual (ex: 10.0 = 10%)
                double valorEstorno = ingresso.getValorPago() * (1 - taxa / 100.0);

                ingresso.setValorEstornado(valorEstorno);
                ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);
                evento.setIngressosDisponiveis(evento.getIngressosDisponiveis() + 1);

                return true;
            }
        }
        return false;
    }

    //Listar ingressos usuário US 15
    public List<Ingresso> listarIngressosUsuario(Long usuarioId) {
        List<Ingresso> ingressos = ingressosPorUsuario.getOrDefault(usuarioId, Collections.emptyList());

        LocalDateTime agora = LocalDateTime.now();

        List<Ingresso> ativos = new ArrayList<>();
        List<Ingresso> inativos = new ArrayList<>();

        for (Ingresso ingresso : ingressos) {
            boolean eventoFinalizado = ingresso.getEvento().getDataFim().isBefore(agora);
            boolean ingressoCancelado = ingresso.isCancelado();

            if (!ingressoCancelado && !eventoFinalizado) {
                ativos.add(ingresso);  // ativo e não realizado
            } else {
                inativos.add(ingresso); // cancelado ou evento já finalizado
            }
        }

        //Ordenar ativos por data de início (ascendente) e depois por nome do evento
        ativos.sort(Comparator
                .comparing((Ingresso i) -> i.getEvento().getDataInicio())
                .thenComparing(i -> i.getEvento().getNome()));

        //Ordenar inativos da mesma forma
        inativos.sort(Comparator
                .comparing((Ingresso i) -> i.getEvento().getDataInicio())
                .thenComparing(i -> i.getEvento().getNome()));

        List<Ingresso> resultado = new ArrayList<>(ativos);
        resultado.addAll(inativos);
        return resultado;
    }

    // [AVALIAÇÃO - Bug] O método 'cancelarTodosIngressosEvento()' cancela os ingressos mas NÃO
    // calcula nem registra o valor de estorno em cada ingresso. A US 10 exige que os usuários
    // sejam reembolsados ao desativar um evento com ingressos vendidos. Falta aplicar a lógica
    // de estorno: ingresso.setValorEstornado(ingresso.getValorPago() * (1 - taxa / 100.0))
    // [AVALIAÇÃO - Qualidade] Evite usar System.out.println() em código de produção. Utilize um
    // framework de logging como SLF4J/Logback: Logger.info("Ingressos do evento {} cancelados.", eventoId);
    public void cancelarTodosIngressosEvento(Long eventoId) {
        // Percorre todos os usuários e cancela ingressos deste evento
        for (Long usuarioId : ingressosPorUsuario.keySet()) {
            List<Ingresso> ingressosUsuario = ingressosPorUsuario.get(usuarioId);
            if (ingressosUsuario != null) {
                ingressosUsuario.stream()
                        .filter(i -> i.getEvento().getId().equals(eventoId) && !i.isCancelado())
                        .forEach(ingresso -> {
                            // Cancela ingresso
                            ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);
                            // Libera vaga (seta capacidade cheia novamente)
                            Evento evento = ingresso.getEvento();
                            evento.setIngressosDisponiveis(evento.getCapacidadeMaxima());
                        });
            }
        }
        System.out.println("Todos os ingressos do evento " + eventoId + " foram cancelados e estornados.");
    }
}
