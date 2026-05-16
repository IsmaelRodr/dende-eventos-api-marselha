package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.repositories.mappers.EventoRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository;

import java.sql.*;
import java.util.Optional;

public class EventoRepository implements CrudRepository<Evento, Long> {

    private final EventoRowMapper mapper = new EventoRowMapper();

    @Override
    public Evento save(Evento evento) {
        String sql = "INSERT INTO eventos (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim, tipo_evento, modalidade, preco_unitario, taxa_cancelamento, evento_estorno, capacidade_maxima, local_evento, evento_ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, evento.getOrganizador().getId());
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getDescricao());
            stmt.setString(4, evento.getPaginaWeb());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(evento.getDataFim()));
            stmt.setString(7, evento.getTipoEvento().name());
            stmt.setString(8, evento.getModalidade().name());
            stmt.setDouble(9, evento.getPrecoUnitarioIngresso());
            stmt.setDouble(10, evento.getTaxaCancelamento());
            stmt.setBoolean(11, evento.isEventoEstorno());
            stmt.setInt(12, evento.getCapacidadeMaxima());
            stmt.setString(13, evento.getLocalEvento());
            stmt.setBoolean(14, evento.isEventoAtivo());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) evento.setId(rs.getLong(1));
                }
            }
            return evento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o evento.", e);
        }
    }

    @Override
    public Optional<Evento> findById(Long id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("organizador_id"), rs.getString("nome"),
                            rs.getString("descricao"), rs.getString("pagina_web"), rs.getString("data_inicio"),
                            rs.getString("data_fim"), rs.getString("tipo_evento"), rs.getString("modalidade"),
                            rs.getString("preco_unitario"), rs.getString("taxa_cancelamento"), rs.getString("evento_estorno"),
                            rs.getString("capacidade_maxima"), rs.getString("local_evento"), rs.getString("evento_ativo")
                    };
                    return Optional.of(mapper.mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o evento.", e);
        }
        return Optional.empty();
    }

    // ==============================================================================
    // Métodos Stub da Interface (Não necessários para o MVP base agora)
    // ==============================================================================
    @Override public long count() { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Iterable<Evento> findAll() { throw new UnsupportedOperationException(); }
    @Override public Iterable<Evento> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override public <V> Optional<Evento> findByField(String fieldName, V value) { throw new UnsupportedOperationException(); }
    @Override public void delete(Evento entity) { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll(Iterable<? extends Evento> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll() { throw new UnsupportedOperationException(); }
    @Override public Evento update(Evento entity) { throw new UnsupportedOperationException(); }
}