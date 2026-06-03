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
    private final ConnectionPool connectionPool = ConnectionPool.getInstance(); // obtém a instância Singleton

    // ===================== SALVAR =====================
    @Override
    public Evento save(Evento evento) {
        String sql = "INSERT INTO evento (organizador_id, nome, descricao, pagina_web, data_inicio, data_fim, " +
                "tipo_evento, evento_principal_id, modalidade, preco_unitario_ingresso, taxa_cancelamento, " +
                "evento_estorno, capacidade_maxima, local_evento, evento_ativo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();  // método de instância
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, evento.getOrganizador().getId());
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getDescricao());
            stmt.setString(4, evento.getPaginaWeb());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(evento.getDataFim()));
            stmt.setString(7, evento.getTipoEvento() != null ? evento.getTipoEvento().name() : null);
            stmt.setObject(8, evento.getEventoPrincipal() != null ? evento.getEventoPrincipal().getId() : null);
            stmt.setString(9, evento.getModalidade() != null ? evento.getModalidade().name() : null);
            stmt.setDouble(10, evento.getPrecoUnitarioIngresso());
            stmt.setDouble(11, evento.getTaxaCancelamento());
            stmt.setBoolean(12, evento.isEventoEstorno());
            stmt.setInt(13, evento.getCapacidadeMaxima());
            stmt.setString(14, evento.getLocalEvento());
            stmt.setBoolean(15, evento.isEventoAtivo());


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
        String sql = "SELECT * FROM evento WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Evento evento = mapEventoBasico(rs);
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
        String sql = "UPDATE evento SET nome = ?, descricao = ?, pagina_web = ?, data_inicio = ?, data_fim = ?, " +
                "tipo_evento = ?, evento_principal_id = ?,  modalidade = ?, preco_unitario_ingresso = ?, taxa_cancelamento = ?, evento_estorno = ?, " +
                "capacidade_maxima = ?, local_evento = ?, evento_ativo = ? " + "WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getPaginaWeb());
            stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataFim()));
            stmt.setString(6, evento.getTipoEvento() != null ? evento.getTipoEvento().name() : null);
            stmt.setObject(7, evento.getEventoPrincipal() != null ? evento.getEventoPrincipal().getId() : null);
            stmt.setString(8, evento.getModalidade() != null ? evento.getModalidade().name() : null);
            stmt.setDouble(9, evento.getPrecoUnitarioIngresso());
            stmt.setDouble(10, evento.getTaxaCancelamento());
            stmt.setBoolean(11, evento.isEventoEstorno());
            stmt.setInt(12, evento.getCapacidadeMaxima());
            stmt.setString(13, evento.getLocalEvento());
            stmt.setBoolean(14, evento.isEventoAtivo());
            stmt.setLong(15, evento.getId());

            stmt.executeUpdate();
            return evento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o evento.", e);
        }
    }

    // ===================== LISTAR EVENTOS ATIVOS (feed) =====================
    public List<Evento> findAllAtivos() {
        String sql = """
            SELECT e.id, e.organizador_id, e.nome, e.descricao, e.pagina_web,
                   e.data_inicio, e.data_fim, e.tipo_evento, e.evento_principal_id, e.modalidade,
                   e.preco_unitario_ingresso, e.taxa_cancelamento, e.evento_estorno,
                   e.capacidade_maxima, e.local_evento, e.evento_ativo,
                   (e.capacidade_maxima - COALESCE(
                       (SELECT COUNT(*) FROM ingresso i 
                        WHERE i.evento_id = e.id AND i.status = 'ACEITO'), 0)
                   ) AS ingressos_disponiveis_calculado
            FROM evento e
            WHERE e.evento_ativo = TRUE
              AND e.data_fim > NOW()
              AND e.capacidade_maxima > (
                  SELECT COALESCE(COUNT(*), 0) FROM ingresso i
                  WHERE i.evento_id = e.id AND i.status = 'ACEITO'
              )
            ORDER BY e.data_inicio, e.nome
        """;
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] row = {
                        rs.getString("id"), rs.getString("organizador_id"), rs.getString("nome"),
                        rs.getString("descricao"), rs.getString("pagina_web"), rs.getString("data_inicio"),
                        rs.getString("data_fim"), rs.getString("tipo_evento"), rs.getString("evento_principal_id"),
                        rs.getString("modalidade"), rs.getString("preco_unitario_ingresso"), rs.getString("taxa_cancelamento"),
                        rs.getString("evento_estorno"), rs.getString("capacidade_maxima"), rs.getString("local_evento"),
                        rs.getString("evento_ativo")
                };
                Evento evento = mapper.mapRow(row);
                int disponiveis = rs.getInt("ingressos_disponiveis_calculado");
                evento.setIngressosDisponiveis(disponiveis);
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
        String sql = "SELECT * FROM evento WHERE organizador_id = ?";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
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
                   e.preco_unitario_ingresso, e.taxa_cancelamento, e.evento_estorno,
                   e.capacidade_maxima, e.local_evento, e.evento_ativo, e.evento_principal_id,
                   i.id AS ingresso_id, i.usuario_id, i.valor_pago, i.valor_estornado,
                   i.data_compra, i.status, i.email
            FROM evento e
            LEFT JOIN ingresso i ON e.id = i.evento_id
            WHERE e.id = ?
            """;

        try (Connection conn = connectionPool.getConnection();
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
        evento.setPrecoUnitarioIngresso(rs.getDouble("preco_unitario_ingresso"));
        evento.setTaxaCancelamento(rs.getDouble("taxa_cancelamento"));
        evento.setEventoEstorno(rs.getBoolean("evento_estorno"));
        evento.setCapacidadeMaxima(rs.getInt("capacidade_maxima"));
        evento.setLocalEvento(rs.getString("local_evento"));
        evento.setEventoAtivo(rs.getBoolean("evento_ativo"));
        return evento;
    }

    public Optional<Evento> findByIdComIngressosDisponiveis(Long id) {
        String sql = """
        SELECT e.id, e.organizador_id, e.nome, e.descricao, e.pagina_web,
               e.data_inicio, e.data_fim, e.tipo_evento, e.evento_principal_id,
               e.modalidade, e.preco_unitario_ingresso, e.taxa_cancelamento,
               e.evento_estorno, e.capacidade_maxima, e.local_evento, e.evento_ativo,
               (e.capacidade_maxima - COALESCE(
                   (SELECT COUNT(*) FROM ingresso i 
                    WHERE i.evento_id = e.id AND i.status = 'ACEITO'), 0)
               ) AS ingressos_disponiveis_calculado
        FROM evento e
        WHERE e.id = ?
    """;
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Evento evento = mapEventoBasico(rs);
                    evento.setIngressosDisponiveis(rs.getInt("ingressos_disponiveis_calculado"));
                    carregarOrganizadorBasico(evento, rs.getLong("organizador_id"));
                    Long idPrincipal = rs.getLong("evento_principal_id");
                    if (!rs.wasNull()) {
                        carregarEventoPrincipalBasico(evento, idPrincipal);
                    }
                    return Optional.of(evento);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar evento com disponibilidade.", e);
        }
        return Optional.empty();
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