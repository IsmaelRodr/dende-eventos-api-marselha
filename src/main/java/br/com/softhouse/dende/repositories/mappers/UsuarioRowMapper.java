package br.com.softhouse.dende.repositories.mappers;

import br.com.dende.softhouse.repositorry.RowMapper;
import br.com.softhouse.dende.model.Usuario;

import java.time.LocalDate;

public class UsuarioRowMapper implements RowMapper<Usuario> {

    @Override
    public Usuario mapRow(String[] row) {
        // O array de Strings representa as colunas do seu SELECT na ordem em que foram chamadas
        Usuario usuario = new Usuario();
        
        usuario.setId(Long.parseLong(row[0]));
        usuario.setNome(row[1]);
        usuario.setDataNascimento(LocalDate.parse(row[2])); // Converte a String do banco para LocalDate
        usuario.setSexo(row[3]);
        usuario.setEmail(row[4]);
        usuario.setSenha(row[5]);
        
        // Converte "1" ou "true" do banco para boolean
        usuario.setAtivo(Boolean.parseBoolean(row[6]) || "1".equals(row[6])); 
        
        return usuario;
    }
}