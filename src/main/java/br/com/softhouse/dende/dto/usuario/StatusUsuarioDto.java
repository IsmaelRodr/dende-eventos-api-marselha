package br.com.softhouse.dende.dto.usuario;

public record StatusUsuarioDto(
        String mensagem,
        Long usuarioId,
        Boolean ativo
) {}
