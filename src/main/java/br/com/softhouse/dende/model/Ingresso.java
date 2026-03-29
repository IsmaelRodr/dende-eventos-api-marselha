package br.com.softhouse.dende.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ingresso {

    private Long id;
    private Usuario usuario;
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

    public Ingresso() {}

    public Ingresso(Long id, Usuario usuario, Evento evento, double valorPago, String email) {
        this.id = id;
        this.usuario = Objects.requireNonNull(usuario);
        this.evento = Objects.requireNonNull(evento);
        this.valorPago = valorPago;
        this.email = email;
        this.status = StatusIngresso.ACEITO;
        this.dataCompra = LocalDateTime.now();

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public Evento getEvento() { return evento; }

    public StatusIngresso getStatus() { return status; }
    public void setStatus(StatusIngresso status) { this.status = status; }

    public double getValorPago() { return valorPago; }

    public void setValorEstornado(double valorEstornado) { this.valorEstornado = valorEstornado; }
    public double getValorEstornado() { return valorEstornado; }

    public LocalDateTime getDataCompra() {return dataCompra;}

    public String getEmail(){ return email;}

    public boolean isCancelado() {
        return status == StatusIngresso.CANCELADO;
    }
}