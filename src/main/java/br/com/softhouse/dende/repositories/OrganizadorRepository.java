package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.mappers.OrganizadorRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository; // O import com dois R's!

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizadorRepository implements CrudRepository<Organizador, Long> {

    private final OrganizadorRowMapper mapper = new OrganizadorRowMapper();

    @Override
    public Organizador save(Organizador org) {
        // SQL que salva tanto os dados de usuário quanto os de empresa do organizador
        String sql = "INSERT INTO organizadores (nome, data_nascimento, sexo, email, senha, ativo, cnpj, razao_social, nome_fantasia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, org.getNome());
            stmt.setDate(2, Date.valueOf(org.getDataNascimento()));
            stmt.setString(3, org.getSexo());
            stmt.setString(4, org.getEmail());
            stmt.setString(5, org.getSenha());
            stmt.setBoolean(6, org.isAtivo());
            stmt.setString(7, org.getEmpresa().getCnpj());
            stmt.setString(8, org.getEmpresa().getRazaoSocial());
            stmt.setString(9, org.getEmpresa().getNomeFantasia());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) org.setId(rs.getLong(1));
            }
            return org;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar organizador", e);
        }
    }

    @Override
    public Optional<Organizador> findById(Long id) {
        String sql = "SELECT * FROM organizadores WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("nome"), rs.getString("data_nascimento"),
                            rs.getString("sexo"), rs.getString("email"), rs.getString("senha"),
                            rs.getString("ativo"), rs.getString("cnpj"), rs.getString("razao_social"),
                            rs.getString("nome_fantasia")
                    };
                    return Optional.of(mapper.mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar organizador", e);
        }
        return Optional.empty();
    }

    // Métodos obrigatórios que você pode deixar como UnsupportedOperationException por enquanto para compilar rápido
    @Override public long count() { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Iterable<Organizador> findAll() { throw new UnsupportedOperationException(); }
    @Override public Iterable<Organizador> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override public <V> Optional<Organizador> findByField(String fieldName, V value) { throw new UnsupportedOperationException(); }
    @Override public void delete(Organizador entity) { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll(Iterable<? extends Organizador> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll() { throw new UnsupportedOperationException(); }
    @Override public Organizador update(Organizador entity) { throw new UnsupportedOperationException(); }
}