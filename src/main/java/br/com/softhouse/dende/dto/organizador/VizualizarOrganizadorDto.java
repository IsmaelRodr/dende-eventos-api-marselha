package br.com.softhouse.dende.dto.organizador;

import br.com.softhouse.dende.model.Empresa;

import java.time.LocalDate;

public record VizualizarOrganizadorDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String email,
        EmpresaDto empresa
) {}
