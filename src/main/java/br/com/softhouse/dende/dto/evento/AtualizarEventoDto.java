package br.com.softhouse.dende.dto.evento;

import java.time.LocalDateTime;

public record AtualizarEventoDto(
        String nome,
        String descricao,
        String paginaWeb,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String tipoEvento,
        Long eventoPrincipalId,
        String modalidade,
        Double precoUnitarioIngresso,
        Double taxaCancelamento,
        boolean eventoEstorno,
        Integer capacidadeMaxima,
        String localEvento
) {}
