package br.com.softhouse.dende.dto.organizador;

import br.com.softhouse.dende.model.Empresa;

import java.time.LocalDate;

public record CadastrarOrganizadorDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String email,
        String senha,
        boolean ativo,
        Empresa empresa
) {}
