package br.com.softhouse.dende.dto.evento;

import java.time.LocalDateTime;

public record FeedEventoDto(
        String nome,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String local,
        Double precoIngresso,
        Integer capacidadeMaxima,
        String tipoEvento,
        String modalidade
) {}
