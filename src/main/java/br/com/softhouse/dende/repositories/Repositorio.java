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

    public Usuario buscarUsuarioPorId(Long id) {
        return usuariosComum.get(id);
    }

    public Organizador buscarOrganizadorPorId(Long id) {
        return organizadores.get(id);
    }

    public boolean emailExiste(String email) {
        for (Usuario u : usuariosComum.values()) {
            if (email != null && email.equalsIgnoreCase(u.getEmail())) return true;
        }
        for (Organizador o : organizadores.values()) {
            if (email != null && email.equalsIgnoreCase(o.getEmail())) return true;
        }
        return false;
    }

    // --- MÉTODOS DE ATUALIZAR COMPLETOS (Pedido do Líder) ---

    public void atualizarDadosUsuario(Usuario usuarioExistente, Usuario novosDados) {
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
        Organizador organizador = organizadores.get(organizadorId);

        if (organizador == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (evento.getId() == null || evento.getId() == 0) {
            evento.setId(contadorEventos++);
        }

        organizador.addEvento(evento);
        List<Evento> lista = eventos.computeIfAbsent(organizadorId, o -> new ArrayList<>());

        if (!lista.contains(evento)) {
            lista.add(evento);
        }
    }

    public void atualizarEvento(long organizadorId , Evento evento, long eventoId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Evento eventoExistente = lista.stream().filter(e -> e.getId() == eventoId).findFirst().orElseThrow();

        if (evento.getDataInicio() != null && evento.getDataFim() != null) {
            if (evento.getDataFim().isBefore(evento.getDataInicio())) {
                throw new IllegalArgumentException("Data fim não pode ser anterior à data início.");
            } if (Duration.between(evento.getDataInicio(), evento.getDataFim()).toMinutes() < 30) {
                throw new IllegalArgumentException("Evento deve ter no mínimo 30 minutos.");
            }
        }

        if (evento.getNome() != null)
            eventoExistente.setNome(evento.getNome());
        if (evento.getDescricao() != null)
            eventoExistente.setDescricao(evento.getDescricao());
        if (evento.getPaginaWeb() != null)
            eventoExistente.setPaginaWeb(evento.getPaginaWeb());
        if (evento.getDataInicio() != null)
            eventoExistente.setDataInicio(evento.getDataInicio());
        if (evento.getDataFim() != null)
            eventoExistente.setDataFim(evento.getDataFim());
        if (evento.getTipoEvento() != null)
            eventoExistente.setTipoEvento(evento.getTipoEvento());
        if (evento.getEventoPrincipal() != null)
            eventoExistente.setEventoPrincipal(evento.getEventoPrincipal());
        if (evento.getModalidade() != null)
            eventoExistente.setModalidade(evento.getModalidade());
        if (evento.getLocalEvento() != null)
            eventoExistente.setLocalEvento(evento.getLocalEvento());
        eventoExistente.setPrecoUnitarioIngresso(evento.getPrecoUnitarioIngresso());
        eventoExistente.setTaxaCancelamento(evento.getTaxaCancelamento());
        eventoExistente.setEventoEstorno(evento.isEventoEstorno());
        eventoExistente.setCapacidadeMaxima(evento.getCapacidadeMaxima());
    }

    public Evento buscarEvento(Long id) {
        return encontrarEvento(id);
    }

    public void ativarEvento(long eventoId, long organizadorId){
        List<Evento> lista = eventos.get(organizadorId);

        if (lista == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

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

    public List<Evento> listarEventoPorOrganizador(Long organizadorId) {
        Organizador organizador = organizadores.get(organizadorId);

        if (organizador == null) {
            return Collections.emptyList();
        }

        return organizador.getEventos();
    }


    public List<Evento> listarEventoAtivos(){
        List<Evento> eventosAtivos = new ArrayList<>();

        for (Organizador org : organizadores.values()) {
            for (Evento evento : org.getEventos()) {
                if (evento.isEventoAtivo() && evento.getIngressosDisponiveis() > 0){
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

        Usuario usuario = ingresso.getUsuario();
        Evento evento = ingresso.getEvento();

        if (usuario == null || evento == null) {
            throw new IllegalArgumentException("Ingresso inválido");
        }

        List<Ingresso> lista = ingressosPorUsuario
                .computeIfAbsent(usuario.getId(), k -> new ArrayList<>());

        if (!lista.contains(ingresso)) {
            lista.add(ingresso);
            usuario.addIngresso(ingresso);
            evento.addIngresso(ingresso);
        }
    }

    private Evento encontrarEvento(Long eventoId) {
        for (Organizador org : organizadores.values()) {
            for (Evento e : org.getEventos()) {
                if (e.getId().equals(eventoId)) {
                    return e;
                }
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

    //Comprar ingresso US 13
    public Map<String, Object> comprarIngresso(Long usuarioId, Long eventoId) {
        Usuario usuario = usuariosComum.get(usuarioId);
        Evento evento = encontrarEvento(eventoId);

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

    //Cancelar ingresso US 14
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

    public void cancelarTodosIngressosEvento(Long eventoId) {
        // Percorre todos os usuários e cancela ingressos deste evento
        for (Long usuarioId : ingressosPorUsuario.keySet()) {
            List<Ingresso> ingressosUsuario = ingressosPorUsuario.get(usuarioId);
            if (ingressosUsuario != null) {
                ingressosUsuario.stream()
                        .filter(i -> i.getEvento().getId().equals(eventoId) && !i.isCancelado())
                        .forEach(ingresso -> {
                            ingresso.setStatus(Ingresso.StatusIngresso.CANCELADO);

                            Evento evento = ingresso.getEvento();

                            double valorEstorno;
                            if (evento.isEventoEstorno()) {
                                double taxa = evento.getTaxaCancelamento();
                                valorEstorno = ingresso.getValorPago() * (1 - taxa / 100.0);
                            } else {
                                valorEstorno = 0.0;
                            }

                            ingresso.setValorEstornado(valorEstorno);
                        });
            }
        }
        System.out.println("Todos os ingressos do evento " + eventoId + " foram cancelados e estornados.");
    }
}
