package br.com.softhouse.dende.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ingresso {

    private Long id;
    private Usuario usuario;
    private Evento evento;
    private StatusIngresso status;
    private String email;
    private Double valorPago;
    private LocalDateTime dataCompra;
    private Double valorEstornado;


    public enum StatusIngresso {
        ACEITO,
        CANCELADO
    }

    public Ingresso() {}

    public Ingresso(Long id, Usuario usuario, Evento evento, Double valorPago) {
        this.id = Objects.requireNonNull(id);
        this.usuario = Objects.requireNonNull(usuario);
        this.evento = Objects.requireNonNull(evento);
        this.email = usuario.getEmail();
        this.valorPago = valorPago;
        this.status = StatusIngresso.ACEITO;
        this.dataCompra = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public Evento getEvento() { return evento; }

    public StatusIngresso getStatus() { return status; }
    public void setStatus(StatusIngresso status) { this.status = status; }

    public String getEmail() { return email; }
    public Double getValorPago() { return valorPago; }
    public void setValorPago(Double valorPago) { this.valorPago = valorPago; }

    public LocalDateTime getDataCompra() { return dataCompra; }

    public void setValorEstornado(Double valorEstornado) { this.valorEstornado = valorEstornado; }
    public Double getValorEstornado() { return valorEstornado; }

    public boolean isCancelado() {
        return status == StatusIngresso.CANCELADO;
    }
}