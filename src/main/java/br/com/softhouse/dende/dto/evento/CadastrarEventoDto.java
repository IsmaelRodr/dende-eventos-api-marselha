package br.com.softhouse.dende.dto.evento;

import java.time.LocalDateTime;

public record CadastrarEventoDto(
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
        Boolean eventoEstorno,
        Integer capacidadeMaxima,
        String localEvento
) {}
