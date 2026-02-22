package br.com.softhouse.dende.model;

import java.time.LocalDateTime;

public class Evento {

    private Long id;
    private Long organizador ;
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

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOrganizador(Long organizador) { this.organizador = organizador; }
    public Long getOrganizador() {
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

    public int getIngressosDisponiveis() {
        return ingressosDisponiveis;
    }

    public void setIngressosDisponiveis(int ingressosDisponiveis) {
        this.ingressosDisponiveis = ingressosDisponiveis;
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

    public void disponibilizarIngressos(int quantidade) {

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }

        if (quantidade > capacidadeMaxima) {
            throw new IllegalArgumentException("Quantidade excede capacidade.");
        }

        this.ingressosDisponiveis = quantidade;
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
