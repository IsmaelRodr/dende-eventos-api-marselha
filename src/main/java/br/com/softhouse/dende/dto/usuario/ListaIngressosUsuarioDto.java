package br.com.softhouse.dende.dto.usuario;

import java.time.LocalDateTime;

public record ListaIngressosUsuarioDto(
        Long usuarioId,
        Long ingressoId,
        Long eventoId,
        String eventoNome,
        LocalDateTime dataInicio,
        String status,
        Double valorPago,
        Double valorEstornado,
        boolean eventoAtivo,
        LocalDateTime dataCompra,
        String email
) {}
