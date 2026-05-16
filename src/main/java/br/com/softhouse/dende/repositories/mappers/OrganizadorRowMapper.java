package br.com.softhouse.dende.repositories.mappers;

import br.com.dende.softhouse.repositorry.RowMapper;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;

import java.time.LocalDate;

public class OrganizadorRowMapper implements RowMapper<Organizador> {

    @Override
    public Organizador mapRow(String[] row) {
        Organizador organizador = new Organizador();
        
        // Dados básicos do usuário/organizador
        organizador.setId(Long.parseLong(row[0]));
        organizador.setNome(row[1]);
        organizador.setDataNascimento(LocalDate.parse(row[2]));
        organizador.setSexo(row[3]);
        organizador.setEmail(row[4]);
        organizador.setSenha(row[5]);
        
        // Conversão segura de Boolean
        organizador.setAtivo(Boolean.parseBoolean(row[6]) || "1".equals(row[6]));

        // Dados da Empresa (Verificamos se o CNPJ não é nulo/vazio antes de instanciar a Empresa)
        if (row[7] != null && !row[7].trim().isEmpty()) {
            Empresa empresa = new Empresa();
            empresa.setCnpj(row[7]);
            empresa.setRazaoSocial(row[8]);
            empresa.setNomeFantasia(row[9]);
            
            organizador.setEmpresa(empresa);
        }

        return organizador;
    }
}