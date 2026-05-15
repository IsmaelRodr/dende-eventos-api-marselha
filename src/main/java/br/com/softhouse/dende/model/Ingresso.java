package br.com.softhouse.dende.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ingresso {

    private Long id;
    //atributo de associação bidirecional, pois usuario e ingresso se conhecem, mas não
    //dependem uns dos outros para existir.
    private Usuario usuario;
    //atributo todo parte composição, pois o ingresso só existe atrelado a um evento.
    private Evento evento;
    private StatusIngresso status;
    private double valorPago;
    private double valorEstornado;
    private LocalDateTime dataCompra;
    private String email;


    public enum StatusIngresso {
        ACEITO,
        CANCELADO
    }

    //construtor padrão
    public Ingresso() {}

    //construtor com parâmetros (argumentos)
    public Ingresso(Long id, Usuario usuario, Evento evento, double valorPago, String email) {
        this.id = id;
        this.usuario = Objects.requireNonNull(usuario);
        this.evento = Objects.requireNonNull(evento);
        this.valorPago = valorPago;
        this.email = email;
        this.status = StatusIngresso.ACEITO;
        this.dataCompra = LocalDateTime.now();

    }

    //Setters
    public void setId(Long id) { this.id = id; }
    public void setStatus(StatusIngresso status) { this.status = status; }
    public void setValorEstornado(double valorEstornado) { this.valorEstornado = valorEstornado; }

    //Getters
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Evento getEvento() { return evento; }
    public StatusIngresso getStatus() { return status; }
    public double getValorPago() { return valorPago; }
    public double getValorEstornado() { return valorEstornado; }
    public LocalDateTime getDataCompra() {return dataCompra;}
    public String getEmail(){ return email;}

    //Booleans
    public boolean isCancelado() {
        return status == StatusIngresso.CANCELADO;
    }

    //equals para comparação de instâncias
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Ingresso that = (Ingresso) obj;
        return id != null && id.equals(that.id);
    }

    //hashcode para representar unicamente a instância
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //toString para representar textualmente a instância
    @Override
    public String toString() {
        return "Ingresso{" +
                "id=" + id +
                ", usuarioEmail='" + (usuario != null ? usuario.getEmail() : null) + '\'' +
                ", eventoNome='" + (evento != null ? evento.getNome() : null) + '\'' +
                ", status=" + status +
                ", valorPago=" + valorPago +
                ", valorEstornado=" + valorEstornado +
                ", dataCompra=" + dataCompra +
                '}';
    }
}