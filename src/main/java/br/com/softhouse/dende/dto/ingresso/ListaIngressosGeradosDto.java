package br.com.softhouse.dende.dto.ingresso;

import java.util.List;

public record ListaIngressosGeradosDto(
        Double valorTotal,
        List<IngressoGeradoDto> ingressosGerados
) {}
