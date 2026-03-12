package br.com.softhouse.dende.dto.organizador;

public record StatusOrganizadorDto(
        String mensagem,
        Long organizadorId,
        boolean ativo
) {}
