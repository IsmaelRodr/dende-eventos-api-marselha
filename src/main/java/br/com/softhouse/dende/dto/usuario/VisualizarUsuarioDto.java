package br.com.softhouse.dende.dto.usuario;

import java.time.LocalDate;

public record VisualizarUsuarioDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String email
) {}
