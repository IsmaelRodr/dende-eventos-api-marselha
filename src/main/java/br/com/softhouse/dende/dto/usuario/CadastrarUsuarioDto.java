package br.com.softhouse.dende.dto.usuario;

import java.time.LocalDate;

public record CadastrarUsuarioDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String email,
        String senha
) {}
