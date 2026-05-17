package br.com.softhouse.dende.model;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.evento.EventoJaAtivoException;
import br.com.softhouse.dende.exceptions.evento.EventoJaInativoException;
import br.com.softhouse.dende.exceptions.evento.EventoSemIngressosDisponiveisException;
import br.com.softhouse.dende.exceptions.ingresso.CancelamentoNaoPermitidoException;
import br.com.softhouse.dende.exceptions.ingresso.IngressoJaCanceladoException;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class Evento {

    private Long id;
    private Organizador organizador;
    private String nome;
    private String descricao;
    private String paginaWeb;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private TipoEvento tipoEvento;
    private Evento eventoPrincipal;
    private Modalidade modalidade;
    private double precoUnitarioIngresso;
    private double taxaCancelamento;
    private boolean eventoEstorno;
    private int capacidadeMaxima;
    private int ingressosDisponiveis;
    private String localEvento;
    private boolean eventoAtivo = false;
    private final List<Ingresso> ingressos = new ArrayList<>();

    public enum TipoEvento {
        SOCIAL, CORPORATIVO, ACADEMICO, CULTURAL, ENTRETENIMENTO,
        RELIGIOSOS, ESPORTIVOS, FEIRA, CONGRESSO, OFICINA,
        CURSO, TREINAMENTO, AULA, SEMINARIO, PALESTRA,
        SHOW, FESTIVAL, EXPOSICAO, RETIRO, CULTO,
        CELEBRACAO, CAMPEONATO, CORRIDA
    }

    public enum Modalidade {
        PRESENCIAL, REMOTO, HIBRIDO
    }

    public Evento() {}

    // Builder manual (sem lógica de negócio, apenas construção)
    public static EventoBuilder builder() {
        return new EventoBuilder();
    }

    public static class EventoBuilder {
        private Long id;
        private Organizador organizador;
        private String nome;
        private String descricao;
        private String paginaWeb;
        private LocalDateTime dataInicio;
        private LocalDateTime dataFim;
        private TipoEvento tipoEvento;
        private Evento eventoPrincipal;
        private Modalidade modalidade;
        private double precoUnitarioIngresso;
        private double taxaCancelamento;
        private boolean eventoEstorno;
        private int capacidadeMaxima;
        private String localEvento;

        public EventoBuilder id(Long id) { this.id = id; return this; }
        public EventoBuilder organizador(Organizador organizador) { this.organizador = organizador; return this; }
        public EventoBuilder nome(String nome) { this.nome = nome; return this; }
        public EventoBuilder descricao(String descricao) { this.descricao = descricao; return this; }
        public EventoBuilder paginaWeb(String paginaWeb) { this.paginaWeb = paginaWeb; return this; }
        public EventoBuilder dataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; return this; }
        public EventoBuilder dataFim(LocalDateTime dataFim) { this.dataFim = dataFim; return this; }
        public EventoBuilder tipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; return this; }
        public EventoBuilder eventoPrincipal(Evento eventoPrincipal) { this.eventoPrincipal = eventoPrincipal; return this; }
        public EventoBuilder modalidade(Modalidade modalidade) { this.modalidade = modalidade; return this; }
        public EventoBuilder precoUnitarioIngresso(double precoUnitarioIngresso) { this.precoUnitarioIngresso = precoUnitarioIngresso; return this; }
        public EventoBuilder taxaCancelamento(double taxaCancelamento) { this.taxaCancelamento = taxaCancelamento; return this; }
        public EventoBuilder eventoEstorno(boolean eventoEstorno) { this.eventoEstorno = eventoEstorno; return this; }
        public EventoBuilder capacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; return this; }
        public EventoBuilder localEvento(String localEvento) { this.localEvento = localEvento; return this; }

        public Evento build() {
            Evento evento = new Evento();
            evento.id = this.id;
            evento.organizador = this.organizador;
            evento.nome = this.nome;
            evento.descricao = this.descricao;
            evento.paginaWeb = this.paginaWeb;
            evento.dataInicio = this.dataInicio;
            evento.dataFim = this.dataFim;
            evento.tipoEvento = this.tipoEvento;
            evento.eventoPrincipal = this.eventoPrincipal;
            evento.modalidade = this.modalidade;
            evento.precoUnitarioIngresso = this.precoUnitarioIngresso;
            evento.taxaCancelamento = this.taxaCancelamento;
            evento.eventoEstorno = this.eventoEstorno;
            evento.capacidadeMaxima = this.capacidadeMaxima;
            evento.localEvento = this.localEvento;
            // ingressosDisponiveis e eventoAtivo são gerenciados pelo service
            return evento;
        }
    }

    public List<Ingresso> getIngressos() {
        return List.copyOf(ingressos);
    }

    public void setIngressos(List<Ingresso> ingressos) {
        this.ingressos.clear();
        if (ingressos != null) {
            this.ingressos.addAll(ingressos);
        }
    }

    public void ativar() {
        if (this.isEventoAtivo()) {
            throw new EventoJaAtivoException("Evento já está ativo.");
        }
        setEventoAtivo(true);
        ingressosDisponiveis = capacidadeMaxima;
    }

    public List<Ingresso> desativar() {
        if (!this.isEventoAtivo()) {
            throw new EventoJaInativoException("Evento já está inativo.");
        }
        List<Ingresso> cancelados = cancelarIngressos();
        setEventoAtivo(false);
        ingressosDisponiveis = 0;
        return cancelados;
    }

    private List<Ingresso> cancelarIngressos() {
        List<Ingresso> cancelados = new ArrayList<>();
        for (Ingresso ingresso : ingressos) {
            if (!ingresso.isCancelado()) {
                ingresso.cancelar();
                cancelados.add(ingresso);
            }
        }
        return cancelados;
    }


    public void adicionarIngresso(Ingresso ingresso) {
        if (ingressosDisponiveis<= 0){
            throw new EventoSemIngressosDisponiveisException("Ingressos esgotados.");
        }
        if (!ingressos.contains(ingresso)){
            ingressos.add(ingresso);
            ingressosDisponiveis--;
            ingresso.setEvento(this);
        }
    }

    public void cancelarIngressoIndividual(Ingresso ingresso) {
        if (!this.eventoEstorno) {
            throw new CancelamentoNaoPermitidoException("Evento não permite estorno.");
        }
        ingresso.cancelar();
        this.ingressosDisponiveis++;
    }


    //Ainda na ideia da coleção, precisaria ser adicionado 2 métodos
    //1 para disponibilizar todos os ingressos do ponto de vista de realizar (ativar) um evento
    //1 para cancelar todos os ingressos ao cancelar (desativar) um evento, com devolução.

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evento that = (Evento) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", tipoEvento=" + tipoEvento +
                ", modalidade=" + modalidade +
                ", preco=" + precoUnitarioIngresso +
                ", ativo=" + eventoAtivo +
                ", ingressosDisponiveis=" + ingressosDisponiveis +
                '}';
    }
}