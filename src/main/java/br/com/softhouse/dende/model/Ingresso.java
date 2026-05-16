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

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public StatusIngresso getStatus() { return status; }
    public void setStatus(StatusIngresso status) { this.status = status; }
    public double getValorPago() { return valorPago; }
    public void setValorPago(double valorPago) { this.valorPago = valorPago; }
    public double getValorEstornado() { return valorEstornado; }
    public void setValorEstornado(double valorEstornado) { this.valorEstornado = valorEstornado; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // --- SETTERS FALTANTES PARA O BANCO DE DADOS ---
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public void setValorPago(double valorPago) { this.valorPago = valorPago; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isCancelado() {
        return status == StatusIngresso.CANCELADO;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingresso that = (Ingresso) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

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