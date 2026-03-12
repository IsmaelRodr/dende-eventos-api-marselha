package br.com.softhouse.dende.dto.usuario;

import java.time.LocalDate;

public record AtualizarUsuarioDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String senha
) {
}
