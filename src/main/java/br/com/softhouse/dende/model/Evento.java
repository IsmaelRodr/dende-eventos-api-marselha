package br.com.softhouse.dende.model;

import java.time.LocalDateTime;

public class Evento {

    private Long contadorId = 1L;

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
    private boolean eventoAtivo;

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
            final Organizador organizador,
            final String nome,
            final String descricao,
            final String paginaWeb,
            final LocalDateTime dataInicio,
            final LocalDateTime dataFim,
            final TipoEvento tipoEvento,
            final Evento eventoPrincipal,
            final Modalidade modalidade,
            final double precoUnitarioIngresso,
            final double taxaCancelamento,
            final boolean eventoEstorno,
            final int capacidadeMaxima,
            final String localEvento
    ){
        this.id = contadorId++;
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
    }

    public long getId() {
        return id;
    }

    public Organizador getOrganizador() {
        return organizador;
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

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Evento getEventoPrincipal() {
        return eventoPrincipal;
    }

    public void setEventoPrincipal(Evento eventoPrincipal) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Evento evento = (Evento) o;
        return getId() == evento.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
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
                '}';
    }
}