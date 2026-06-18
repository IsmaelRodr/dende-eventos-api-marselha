package br.com.softhouse.dende.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ingresso {

    private Long id;
    private Usuario usuario;
    private Evento evento;
    private StatusIngresso status;
    // [AVALIAÇÃO - Item 14] O tipo Double não é adequado para valores financeiros por problemas de precisão
    // de ponto flutuante. Para valores monetários, utilize BigDecimal.
    // A nova declaração ficaria assim: private BigDecimal valorPago;
    private Double valorPago;
    // [AVALIAÇÃO - Item 14] O mesmo problema se aplica ao atributo 'valorEstornado'. Utilize BigDecimal.
    // A nova declaração ficaria assim: private BigDecimal valorEstornado;
    private Double valorEstornado;
    private LocalDateTime dataCompra;
    // [AVALIAÇÃO - Item 4] O atributo 'email' é redundante nesta classe. Como 'Ingresso' já possui uma
    // referência ao objeto 'Usuario', o e-mail pode ser acessado diretamente via 'usuario.getEmail()'.
    // Manter este campo duplica a informação e pode gerar inconsistências (e-mail do Ingresso diferente
    // do e-mail do Usuário associado).
    // Sugestão: remova o atributo 'email' e utilize 'this.usuario.getEmail()' onde necessário.
    private String email;


    public enum StatusIngresso {
        ACEITO,
        CANCELADO
    }

    public Ingresso() {}

    // [AVALIAÇÃO - Item 4] O parâmetro 'email' neste construtor é desnecessário, pois o e-mail pode ser
    // obtido diretamente de 'usuario.getEmail()'. Aceitar um e-mail externo abre a possibilidade de
    // inconsistência (ex: passar um e-mail diferente do usuário associado ao ingresso).
    // Sugestão: remova o parâmetro 'email' e utilize internamente: this.email = usuario.getEmail();
    // Melhor ainda: remova o atributo 'email' da classe completamente (ver comentário no atributo acima).
    // [AVALIAÇÃO - Item 5] A classe Ingresso está sendo serializada diretamente como resposta. Uma boa prática
    // seria criar um 'IngressoResponseDTO' e um 'IngressoMapper' para converter Ingresso -> DTO,
    // separando a representação interna da entidade do que é exposto na API. Não era obrigatório nesta
    // avaliação, mas é altamente recomendado para organização e manutenibilidade.
    public Ingresso(Long id, Usuario usuario, Evento evento, Double valorPago, String email) {
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

    public Double getValorPago() { return valorPago; }

    public void setValorEstornado(Double valorEstornado) { this.valorEstornado = valorEstornado; }
    public Double getValorEstornado() { return valorEstornado; }

    public LocalDateTime getDataCompra() {return dataCompra;}

    public String getEmail(){ return email;}

    public boolean isCancelado() {
        return status == StatusIngresso.CANCELADO;
    }
}