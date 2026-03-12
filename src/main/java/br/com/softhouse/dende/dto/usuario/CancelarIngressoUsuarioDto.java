package br.com.softhouse.dende.dto.usuario;

public record CancelarIngressoUsuarioDto(
        String mensagem,
        Long ingressoId,
        Double valorEstornado
) {}
