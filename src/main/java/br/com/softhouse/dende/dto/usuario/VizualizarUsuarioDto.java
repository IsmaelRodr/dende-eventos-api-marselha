package br.com.softhouse.dende.dto.usuario;

import java.time.LocalDate;

public record VizualizarUsuarioDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String email
) {}
