package br.com.softhouse.dende.dto.ingresso;

import java.util.List;

public record ResultadoCompraIngressoDto(
        Double valorTotal,
        List<IngressoGeradoDto> ingressosGerados
) {}
