package br.com.softhouse.dende.repositories.mappers;

import br.com.dende.softhouse.repositorry.RowMapper;
import br.com.softhouse.dende.model.Empresa;

public class EmpresaRowMapper implements RowMapper<Empresa> {

    @Override
    public Empresa mapRow(String[] row) {
        if (row == null || row.length < 4) {
            throw new IllegalArgumentException("Array de dados inválido para mapear Empresa. Esperado pelo menos 4 campos.");
        }
        Empresa empresa = new Empresa();

        empresa.setCnpj(row[0]);                 // cnpj
        empresa.setRazaoSocial(row[1]);          // razao_social
        empresa.setNomeFantasia(row[2]);         // nome_fantasia

        String organizadorIdStr = row[3];
        if (organizadorIdStr != null && !organizadorIdStr.trim().isEmpty()) {
            empresa.setOrganizadorId(Long.parseLong(organizadorIdStr));
        }

        return empresa;
    }
}