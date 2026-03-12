package br.com.softhouse.dende.dto.ingresso;

public record IngressoGeradoDto(
        Long usuarioId,
        Long ingressoId,
        Long eventoId,
        String nomeEvento,
        Double valorPago
) {}
