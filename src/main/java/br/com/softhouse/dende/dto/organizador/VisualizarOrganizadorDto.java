package br.com.softhouse.dende.dto.organizador;

import java.time.LocalDate;

public record VisualizarOrganizadorDto(
        String nome,
        LocalDate dataNascimento,
        String sexo,
        String email,
        EmpresaDto empresa
) {}
