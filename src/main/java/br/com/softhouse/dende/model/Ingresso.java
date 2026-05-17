package br.com.softhouse.dende.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
public class Ingresso {

    private Long id;
    private Usuario usuario;
    private Evento evento;
    private StatusIngresso status;
    private double valorPago;
    private double valorEstornado;
    private LocalDateTime dataCompra;
    private String email;
    //Aqui não temos coleções, mas poderiamos ter os métodos para lidar com a devolução porexemplo.

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