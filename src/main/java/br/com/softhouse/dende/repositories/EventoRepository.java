package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.mappers.EventoRowMapper;
import br.com.softhouse.dende.repositories.mappers.IngressoRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventoRepository implements CrudRepository<Evento, Long> {

    private final EventoRowMapper mapper = new EventoRowMapper();
    private final IngressoRowMapper ingressoMapper = new IngressoRowMapper();


    // ===================== SALVAR =====================
    @Override
    public Evento save(Evento evento) {
        String sql = "INSERT INTO eventos (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim, " +
                "tipo_evento, modalidade, preco_unitario, taxa_cancelamento, evento_estorno, " +
                "capacidade_maxima, ingressos_disponiveis, local_evento, evento_ativo, evento_principal_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, evento.getOrganizador().getId());
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getDescricao());
            stmt.setString(4, evento.getPaginaWeb());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(evento.getDataFim()));
            stmt.setString(7, evento.getTipoEvento() != null ? evento.getTipoEvento().name() : null);
            stmt.setString(8, evento.getModalidade() != null ? evento.getModalidade().name() : null);
            stmt.setDouble(9, evento.getPrecoUnitarioIngresso());
            stmt.setDouble(10, evento.getTaxaCancelamento());
            stmt.setBoolean(11, evento.isEventoEstorno());
            stmt.setInt(12, evento.getCapacidadeMaxima());
            stmt.setInt(13, evento.getIngressosDisponiveis());
            stmt.setString(14, evento.getLocalEvento());
            stmt.setBoolean(15, evento.isEventoAtivo());
            stmt.setObject(16, evento.getEventoPrincipal() != null ? evento.getEventoPrincipal().getId() : null);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        evento.setId(rs.getLong(1));
                    }
                }
            }
            return evento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o evento.", e);
        }
    }

    // ===================== BUSCAR POR ID =====================
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
                            rs.getString("capacidade_maxima"), rs.getString("ingressos_disponiveis"),
                            rs.getString("local_evento"), rs.getString("evento_ativo"), rs.getString("evento_principal_id")
                    };
                    Evento evento = mapper.mapRow(row);
                    carregarOrganizadorBasico(evento, rs.getLong("organizador_id"));
                    Long idPrincipal = rs.getLong("evento_principal_id");
                    if (!rs.wasNull()) {
                        carregarEventoPrincipalBasico(evento, idPrincipal);
                    }
                    return Optional.of(evento);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o evento por ID.", e);
        }
        return Optional.empty();
    }

    // ===================== ATUALIZAR =====================
    @Override
    public Evento update(Evento evento) {
        String sql = "UPDATE eventos SET nome = ?, descricao = ?, pagina_web = ?, data_inicio = ?, data_fim = ?, " +
                "tipo_evento = ?, modalidade = ?, preco_unitario = ?, taxa_cancelamento = ?, evento_estorno = ?, " +
                "capacidade_maxima = ?, ingressos_disponiveis = ?, local_evento = ?, evento_ativo = ?, evento_principal_id = ? " +
                "WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getPaginaWeb());
            stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataFim()));
            stmt.setString(6, evento.getTipoEvento() != null ? evento.getTipoEvento().name() : null);
            stmt.setString(7, evento.getModalidade() != null ? evento.getModalidade().name() : null);
            stmt.setDouble(8, evento.getPrecoUnitarioIngresso());
            stmt.setDouble(9, evento.getTaxaCancelamento());
            stmt.setBoolean(10, evento.isEventoEstorno());
            stmt.setInt(11, evento.getCapacidadeMaxima());
            stmt.setInt(12, evento.getIngressosDisponiveis());
            stmt.setString(13, evento.getLocalEvento());
            stmt.setBoolean(14, evento.isEventoAtivo());
            stmt.setObject(15, evento.getEventoPrincipal() != null ? evento.getEventoPrincipal().getId() : null);
            stmt.setLong(16, evento.getId());

            stmt.executeUpdate();
            return evento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o evento.", e);
        }
    }

    // ===================== LISTAR EVENTOS ATIVOS (feed) =====================
    public List<Evento> findAllAtivos() {
        String sql = "SELECT * FROM eventos WHERE evento_ativo = TRUE AND data_fim > NOW() AND ingressos_disponiveis > 0 " +
                "ORDER BY data_inicio, nome";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] row = {
                        rs.getString("id"), rs.getString("organizador_id"), rs.getString("nome"),
                        rs.getString("descricao"), rs.getString("pagina_web"), rs.getString("data_inicio"),
                        rs.getString("data_fim"), rs.getString("tipo_evento"), rs.getString("modalidade"),
                        rs.getString("preco_unitario"), rs.getString("taxa_cancelamento"), rs.getString("evento_estorno"),
                        rs.getString("capacidade_maxima"), rs.getString("ingressos_disponiveis"),
                        rs.getString("local_evento"), rs.getString("evento_ativo"), rs.getString("evento_principal_id")
                };
                Evento evento = mapper.mapRow(row);
                carregarOrganizadorBasico(evento, rs.getLong("organizador_id"));
                eventos.add(evento);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos ativos.", e);
        }
        return eventos;
    }

    // ===================== LISTAR EVENTOS DE UM ORGANIZADOR =====================
    public List<Evento> findAllByOrganizadorId(Long organizadorId) {
        String sql = "SELECT * FROM eventos WHERE organizador_id = ?";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, organizadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("organizador_id"), rs.getString("nome"),
                            rs.getString("descricao"), rs.getString("pagina_web"), rs.getString("data_inicio"),
                            rs.getString("data_fim"), rs.getString("tipo_evento"), rs.getString("modalidade"),
                            rs.getString("preco_unitario"), rs.getString("taxa_cancelamento"), rs.getString("evento_estorno"),
                            rs.getString("capacidade_maxima"), rs.getString("ingressos_disponiveis"),
                            rs.getString("local_evento"), rs.getString("evento_ativo"), rs.getString("evento_principal_id")
                    };
                    Evento evento = mapper.mapRow(row);
                    carregarOrganizadorBasico(evento, organizadorId);
                    eventos.add(evento);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos do organizador.", e);
        }
        return eventos;
    }

    public Optional<Evento> findByIdWithIngressos(Long id) {
        String sql = """
            SELECT e.id, e.organizador_id, e.nome, e.descricao, e.pagina_web,
                   e.data_inicio, e.data_fim, e.tipo_evento, e.modalidade,
                   e.preco_unitario, e.taxa_cancelamento, e.evento_estorno,
                   e.capacidade_maxima, e.ingressos_disponiveis, e.local_evento,
                   e.evento_ativo, e.evento_principal_id,
                   i.id AS ingresso_id, i.usuario_id, i.valor_pago, i.valor_estornado,
                   i.data_compra, i.status, i.email
            FROM eventos e
            LEFT JOIN ingressos i ON e.id = i.evento_id
            WHERE e.id = ?
            """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                Evento evento = null;
                List<Ingresso> ingressos = new ArrayList<>();

                while (rs.next()) {
                    if (evento == null) {
                        evento = mapEventoBasico(rs);
                        carregarOrganizadorBasico(evento, rs.getLong("organizador_id"));
                        long idPrincipal = rs.getLong("evento_principal_id");
                        if (!rs.wasNull()) {
                            carregarEventoPrincipalBasico(evento, idPrincipal);
                        }
                    }

                    long ingressoId = rs.getLong("ingresso_id");
                    if (!rs.wasNull()) {
                        String[] rowIngresso = {
                                rs.getString("ingresso_id"),
                                rs.getString("usuario_id"),
                                rs.getString("id"), // evento_id = e.id
                                rs.getString("valor_pago"),
                                rs.getString("valor_estornado"),
                                rs.getString("data_compra"),
                                rs.getString("status"),
                                rs.getString("email")
                        };
                        Ingresso ingresso = ingressoMapper.mapRow(rowIngresso);
                        ingresso.setEvento(evento);
                        ingressos.add(ingresso);
                    }
                }

                if (evento != null) {
                    evento.setIngressos(ingressos);
                    return Optional.of(evento);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar evento com ingressos.", e);
        }
        return Optional.empty();
    }

    private Evento mapEventoBasico(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setNome(rs.getString("nome"));
        evento.setDescricao(rs.getString("descricao"));
        evento.setPaginaWeb(rs.getString("pagina_web"));
        evento.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        evento.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        evento.setTipoEvento(Evento.TipoEvento.valueOf(rs.getString("tipo_evento")));
        evento.setModalidade(Evento.Modalidade.valueOf(rs.getString("modalidade")));
        evento.setPrecoUnitarioIngresso(rs.getDouble("preco_unitario"));
        evento.setTaxaCancelamento(rs.getDouble("taxa_cancelamento"));
        evento.setEventoEstorno(rs.getBoolean("evento_estorno"));
        evento.setCapacidadeMaxima(rs.getInt("capacidade_maxima"));
        evento.setIngressosDisponiveis(rs.getInt("ingressos_disponiveis"));
        evento.setLocalEvento(rs.getString("local_evento"));
        evento.setEventoAtivo(rs.getBoolean("evento_ativo"));
        return evento;
    }

    // ===================== AUXILIARES =====================
    private void carregarOrganizadorBasico(Evento evento, Long organizadorId) {
        Organizador org = new Organizador();
        org.setId(organizadorId);
        evento.setOrganizador(org);
    }

    private void carregarEventoPrincipalBasico(Evento evento, Long idPrincipal) {
        Evento principal = new Evento();
        principal.setId(idPrincipal);
        evento.setEventoPrincipal(principal);
    }

    // ===================== STUBS (não utilizados) =====================
    @Override public long count() { throw new UnsupportedOperationException(); }
    @Override public boolean existsById(Long id) { throw new UnsupportedOperationException(); }
    @Override public Iterable<Evento> findAll() { throw new UnsupportedOperationException(); }
    @Override public Iterable<Evento> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override public <V> Optional<Evento> findByField(String fieldName, V value) { throw new UnsupportedOperationException(); }
    @Override public void delete(Evento entity) { throw new UnsupportedOperationException(); }
    @Override public void deleteById(Long id) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll(Iterable<? extends Evento> entities) { throw new UnsupportedOperationException(); }
    @Override public void deleteAll() { throw new UnsupportedOperationException(); }
}