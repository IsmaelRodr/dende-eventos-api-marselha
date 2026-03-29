package br.com.softhouse.dende.dto.evento;

public record StatusEventoDto(
        String mensagem,
        Long eventoId,
        Boolean ativo
) {}
