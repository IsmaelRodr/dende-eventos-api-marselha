package br.com.softhouse.dende.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Evento {

    private long id;
    private Organizador organizador;
    private String nome;
    private String descricao;
    private String paginaWeb;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private TipoEvento tipoEvento;
    private String eventoPrincipal;
    private Modalidade modalidade;
    private double precoUnitarioIngresso;
    private double taxaCancelamento;
    private boolean eventoEstorno;
    private int capacidadeMaxima;
    private String localEvento;
    private boolean eventoAtivo;
    private Duration duracaoEvento;

    public enum TipoEvento {
        SOCIAL,
        CORPORATIVO,
        ACADEMICO,
        CULTURAL,
        ENTRETENIMENTO,
        RELIGIOSOS,
        ESPORTIVOS,
        FEIRA,
        CONGRESSO,
        OFICINA,
        CURSO,
        TREINAMENTO,
        AULA,
        SEMINARIO,
        PALESTRA,
        SHOW,
        FESTIVAL,
        EXPOSICAO,
        RETIRO,
        CULTO,
        CELEBRACAO,
        CAMPEONATO,
        CORRIDA
    }

    public enum Modalidade{
        PRESENCIAL,
        REMOTO,
        HIBRIDO
    }

    public Evento(){}

    public Evento(
            final long id,
            final Organizador organizador,
            final String nome,
            final String descricao,
            final String paginaWeb,
            final LocalDate dataInicio,
            final LocalDate dataFim,
            final TipoEvento tipoEvento,
            final String eventoPrincipal,
            final Modalidade modalidade,
            final double precoUnitarioIngresso,
            final double taxaCancelamento,
            final boolean eventoEstorno,
            final int capacidadeMaxima,
            final String localEvento,
            final Duration duracaoEvento
    ){
        this.id = id;
        this.organizador = organizador;
        this.nome = nome;
        this.descricao = descricao;
        this.paginaWeb = paginaWeb;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipoEvento = tipoEvento;
        this.eventoPrincipal = eventoPrincipal;
        this.modalidade = modalidade;
        this.precoUnitarioIngresso = precoUnitarioIngresso;
        this.taxaCancelamento = taxaCancelamento;
        this.eventoEstorno = eventoEstorno;
        this.capacidadeMaxima = capacidadeMaxima;
        this.localEvento = localEvento;
        this.eventoAtivo = false;
        this.duracaoEvento = duracaoEvento;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getEventoPrincipal() {
        return eventoPrincipal;
    }

    public void setEventoPrincipal(String eventoPrincipal) {
        this.eventoPrincipal = eventoPrincipal;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(Modalidade modalidade) {
        this.modalidade = modalidade;
    }

    public double getPrecoUnitarioIngresso() {
        return precoUnitarioIngresso;
    }

    public void setPrecoUnitarioIngresso(double precoUnitarioIngresso) {
        this.precoUnitarioIngresso = precoUnitarioIngresso;
    }

    public double getTaxaCancelamento() {
        return taxaCancelamento;
    }

    public void setTaxaCancelamento(double taxaCancelamento) {
        this.taxaCancelamento = taxaCancelamento;
    }

    public boolean isEventoEstorno() {
        return eventoEstorno;
    }

    public void setEventoEstorno(boolean eventoEstorno) {
        this.eventoEstorno = eventoEstorno;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getLocalEvento() {
        return localEvento;
    }

    public void setLocalEvento(String localEvento) {
        this.localEvento = localEvento;
    }

    public boolean isEventoAtivo(){
        return eventoAtivo;
    }

    public void setEventoAtivo(boolean eventoAtivo) {
        this.eventoAtivo = eventoAtivo;
    }

    public Duration getDuracaoEvento() {
        return duracaoEvento;
    }

    public void setDuracaoEvento(Duration duracaoEvento) {
        this.duracaoEvento = duracaoEvento;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id == evento.id && Double.compare(precoUnitarioIngresso, evento.precoUnitarioIngresso) == 0 && Double.compare(taxaCancelamento, evento.taxaCancelamento) == 0 && eventoEstorno == evento.eventoEstorno && capacidadeMaxima == evento.capacidadeMaxima && eventoAtivo == evento.eventoAtivo && Objects.equals(organizador, evento.organizador) && Objects.equals(nome, evento.nome) && Objects.equals(descricao, evento.descricao) && Objects.equals(paginaWeb, evento.paginaWeb) && Objects.equals(dataInicio, evento.dataInicio) && Objects.equals(dataFim, evento.dataFim) && tipoEvento == evento.tipoEvento && Objects.equals(eventoPrincipal, evento.eventoPrincipal) && modalidade == evento.modalidade && Objects.equals(localEvento, evento.localEvento) && Objects.equals(duracaoEvento, evento.duracaoEvento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organizador, nome, descricao, paginaWeb, dataInicio, dataFim, tipoEvento, eventoPrincipal, modalidade, precoUnitarioIngresso, taxaCancelamento, eventoEstorno, capacidadeMaxima, localEvento, eventoAtivo, duracaoEvento);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", organizador=" + organizador +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", paginaWeb='" + paginaWeb + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", tipoEvento=" + tipoEvento +
                ", eventoPrincipal='" + eventoPrincipal + '\'' +
                ", modalidade=" + modalidade +
                ", precoUnitarioIngresso=" + precoUnitarioIngresso +
                ", taxaCancelamento=" + taxaCancelamento +
                ", eventoEstorno=" + eventoEstorno +
                ", capacidadeMaxima=" + capacidadeMaxima +
                ", localEvento='" + localEvento + '\'' +
                ", eventoAtivo=" + eventoAtivo +
                ", duracaoEvento=" + duracaoEvento +
                '}';
    }
}