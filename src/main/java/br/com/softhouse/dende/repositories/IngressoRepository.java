package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.repositories.mappers.IngressoRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository;

import java.sql.*;
import java.util.Optional;

public class IngressoRepository implements CrudRepository<Ingresso, Long> {

    private final IngressoRowMapper mapper = new IngressoRowMapper();

    @Override
    public Ingresso save(Ingresso ingresso) {
        String sql = "INSERT INTO ingressos (usuario_id, evento_id, valor_pago, data_compra, status, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, ingresso.getUsuario().getId());
            stmt.setLong(2, ingresso.getEvento().getId());
            stmt.setDouble(3, ingresso.getValorPago());
            stmt.setTimestamp(4, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.setString(5, ingresso.getStatus().name());
            stmt.setString(6, ingresso.getEmail());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) ingresso.setId(rs.getLong(1));
                }
            }
            return ingresso;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o ingresso.", e);
        }
    }

    @Override
    public Optional<Ingresso> findById(Long id) {
        String sql = "SELECT * FROM ingressos WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("usuario_id"), rs.getString("evento_id"),
                            rs.getString("valor_pago"), rs.getString("data_compra"), rs.getString("status"),
                            rs.getString("email")
                    };
                    return Optional.of(mapper.mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o ingresso.", e);
        }
        return Optional.empty();
    }

    // ==============================================================================
    // Métodos Stub da Interface (Não necessários para o MVP base agora)
    // ==============================================================================
    @Override public long count() { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Iterable<Ingresso> findAll() { throw new UnsupportedOperationException(); }
    @Override public Iterable<Ingresso> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override public <V> Optional<Ingresso> findByField(String fieldName, V value) { throw new UnsupportedOperationException(); }
    @Override public void delete(Ingresso entity) { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll(Iterable<? extends Ingresso> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll() { throw new UnsupportedOperationException(); }
    @Override public Ingresso update(Ingresso entity) { throw new UnsupportedOperationException(); }
}