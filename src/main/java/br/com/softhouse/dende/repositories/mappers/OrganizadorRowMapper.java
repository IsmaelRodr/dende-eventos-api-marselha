package br.com.softhouse.dende.repositories.mappers;

import br.com.dende.softhouse.repositorry.RowMapper;
import br.com.softhouse.dende.model.Organizador;

import java.time.LocalDate;

public class OrganizadorRowMapper implements RowMapper<Organizador> {

    @Override
    public Organizador mapRow(String[] row) {
        if (row == null || row.length < 7) {
            throw new IllegalArgumentException("Array de dados inválido para mapear Organizador. Esperado pelo menos 7 campos.");
        }
        Organizador organizador = new Organizador();

        organizador.setId(Long.parseLong(row[0]));
        organizador.setNome(row[1]);
        organizador.setDataNascimento(LocalDate.parse(row[2]));
        organizador.setSexo(row[3]);
        organizador.setEmail(row[4]);
        organizador.setSenha(row[5]);

        String ativoStr = row[6];
        boolean ativo = "true".equalsIgnoreCase(ativoStr) || "1".equals(ativoStr);
        organizador.setAtivo(ativo);
        return organizador;
    }
}