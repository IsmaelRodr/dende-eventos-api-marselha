package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.mappers.IngressoRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngressoRepository implements CrudRepository<Ingresso, Long> {

    private final IngressoRowMapper mapper = new IngressoRowMapper();

    // ===================== SALVAR =====================
    @Override
    public Ingresso save(Ingresso ingresso) {
        String sql = "INSERT INTO ingressos (usuario_id, evento_id, valor_pago, valor_estornado, data_compra, status, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, ingresso.getUsuario().getId());
            stmt.setLong(2, ingresso.getEvento().getId());
            stmt.setDouble(3, ingresso.getValorPago());
            stmt.setDouble(4, ingresso.getValorEstornado()); // sempre 0.0 no momento da compra
            stmt.setTimestamp(5, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.setString(6, ingresso.getStatus().name());
            stmt.setString(7, ingresso.getEmail());

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

    // ===================== BUSCAR POR ID =====================
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
                            rs.getString("valor_pago"), rs.getString("valor_estornado"),
                            rs.getString("data_compra"), rs.getString("status"), rs.getString("email")
                    };
                    return Optional.of(mapper.mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o ingresso por ID.", e);
        }
        return Optional.empty();
    }

    // ===================== ATUALIZAR =====================
    @Override
    public Ingresso update(Ingresso ingresso) {
        String sql = "UPDATE ingressos SET status = ?, valor_estornado = ?, email = ? WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ingresso.getStatus().name());
            stmt.setDouble(2, ingresso.getValorEstornado());
            stmt.setString(3, ingresso.getEmail());
            stmt.setLong(4, ingresso.getId());

            stmt.executeUpdate();
            return ingresso;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o ingresso.", e);
        }
    }

    // ===================== LISTAR INGRESSOS DE UM USUÁRIO =====================
    public List<Ingresso> findAllByUsuarioId(Long usuarioId) {
        String sql = "SELECT * FROM ingressos WHERE usuario_id = ?";
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("usuario_id"), rs.getString("evento_id"),
                            rs.getString("valor_pago"), rs.getString("valor_estornado"),
                            rs.getString("data_compra"), rs.getString("status"), rs.getString("email")
                    };
                    Ingresso ingresso = mapper.mapRow(row);
                    carregarRelacionamentosBasicos(ingresso, rs.getLong("usuario_id"), rs.getLong("evento_id"));
                    ingressos.add(ingresso);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingressos do usuário.", e);
        }
        return ingressos;
    }

    // ===================== LISTAR INGRESSOS DE UM EVENTO =====================
    public List<Ingresso> findAllByEventoId(Long eventoId) {
        String sql = "SELECT * FROM ingressos WHERE evento_id = ?";
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, eventoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("usuario_id"), rs.getString("evento_id"),
                            rs.getString("valor_pago"), rs.getString("valor_estornado"),
                            rs.getString("data_compra"), rs.getString("status"), rs.getString("email")
                    };
                    Ingresso ingresso = mapper.mapRow(row);
                    carregarRelacionamentosBasicos(ingresso, rs.getLong("usuario_id"), rs.getLong("evento_id"));
                    ingressos.add(ingresso);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingressos do evento.", e);
        }
        return ingressos;
    }

    // ===================== AUXILIARES =====================
    private void carregarRelacionamentosBasicos(Ingresso ingresso, Long usuarioId, Long eventoId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        ingresso.setUsuario(usuario);

        Evento evento = new Evento();
        evento.setId(eventoId);
        ingresso.setEvento(evento);
    }

    // ===================== STUBS (não utilizados) =====================
    @Override public long count() { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Iterable<Ingresso> findAll() { throw new UnsupportedOperationException(); }
    @Override public Iterable<Ingresso> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override public <V> Optional<Ingresso> findByField(String fieldName, V value) { throw new UnsupportedOperationException(); }
    @Override public void delete(Ingresso entity) { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll(Iterable<? extends Ingresso> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll() { throw new UnsupportedOperationException(); }
}