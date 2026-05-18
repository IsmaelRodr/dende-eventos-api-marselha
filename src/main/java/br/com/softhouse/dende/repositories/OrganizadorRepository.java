package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.mappers.EventoRowMapper;
import br.com.softhouse.dende.repositories.mappers.OrganizadorRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.exceptions.repository.PersistenciaException;
import br.com.dende.softhouse.repositorry.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizadorRepository implements CrudRepository<Organizador, Long> {

    private final OrganizadorRowMapper organizadorMapper = new OrganizadorRowMapper();
    private final EventoRowMapper eventoMapper = new EventoRowMapper();
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    // ===================== SAVE =====================
    @Override
    public Organizador save(Organizador org) {
        String sqlOrganizador = "INSERT INTO organizador (nome, data_nascimento, sexo, email, senha, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtOrg = conn.prepareStatement(sqlOrganizador, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrg.setString(1, org.getNome());
                stmtOrg.setDate(2, Date.valueOf(org.getDataNascimento()));
                stmtOrg.setString(3, org.getSexo());
                stmtOrg.setString(4, org.getEmail());
                stmtOrg.setString(5, org.getSenha());
                stmtOrg.setBoolean(6, org.isAtivo());

                int affected = stmtOrg.executeUpdate();
                if (affected == 0) throw new PersistenciaException("Falha ao inserir organizador, nenhuma linha afetada.");

                try (ResultSet generatedKeys = stmtOrg.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        org.setId(generatedKeys.getLong(1));
                    } else {
                        throw new PersistenciaException("Falha ao obter ID do organizador.");
                    }
                }

                // Salva empresa associada, se existir
                if (org.getEmpresa() != null) {
                    salvarEmpresa(conn, org.getId(), org.getEmpresa());
                }

                conn.commit();
                return org;
            } catch (SQLException e) {
                conn.rollback();
                throw new PersistenciaException("Erro ao salvar organizador.");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro de conexão ao salvar organizador.");
        }
    }

    private void salvarEmpresa(Connection conn, Long organizadorId, Empresa empresa) throws SQLException {
        String sqlEmpresa = "INSERT INTO empresa (cnpj, razao_social, nome_fantasia, organizador_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sqlEmpresa)) {
            stmt.setString(1, empresa.getCnpj());
            stmt.setString(2, empresa.getRazaoSocial());
            stmt.setString(3, empresa.getNomeFantasia());
            stmt.setLong(4, organizadorId);
            stmt.executeUpdate();
            empresa.setOrganizadorId(organizadorId); // se a classe Empresa tiver esse campo
        }
    }

    // ===================== UPDATE =====================
    @Override
    public Organizador update(Organizador org) {
        String sqlOrganizador = "UPDATE organizador SET nome=?, data_nascimento=?, sexo=?, email=?, senha=?, ativo=? WHERE id=?";
        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtOrg = conn.prepareStatement(sqlOrganizador)) {
                stmtOrg.setString(1, org.getNome());
                stmtOrg.setDate(2, Date.valueOf(org.getDataNascimento()));
                stmtOrg.setString(3, org.getSexo());
                stmtOrg.setString(4, org.getEmail());
                stmtOrg.setString(5, org.getSenha());
                stmtOrg.setBoolean(6, org.isAtivo());
                stmtOrg.setLong(7, org.getId());

                stmtOrg.executeUpdate();

                // Atualiza ou insere empresa associada
                if (org.getEmpresa() != null) {
                    atualizarEmpresa(conn, org.getId(), org.getEmpresa());
                } else {
                    deletarEmpresa(conn, org.getId());
                }

                conn.commit();
                return org;
            } catch (SQLException e) {
                conn.rollback();
                throw new PersistenciaException("Erro ao atualizar organizador.");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro de conexão ao atualizar organizador.");
        }
    }

    private void atualizarEmpresa(Connection conn, Long organizadorId, Empresa empresa) throws SQLException {
        // Verifica se já existe empresa para este organizador
        String checkSql = "SELECT 1 FROM empresa WHERE organizador_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setLong(1, organizadorId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update
                    String updateSql = "UPDATE empresa SET cnpj=?, razao_social=?, nome_fantasia=? WHERE organizador_id=?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setString(1, empresa.getCnpj());
                        stmt.setString(2, empresa.getRazaoSocial());
                        stmt.setString(3, empresa.getNomeFantasia());
                        stmt.setLong(4, organizadorId);
                        stmt.executeUpdate();
                    }
                } else {
                    // Insert
                    salvarEmpresa(conn, organizadorId, empresa);
                }
            }
        }
    }

    private void deletarEmpresa(Connection conn, Long organizadorId) throws SQLException {
        String sql = "DELETE FROM empresa WHERE organizador_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, organizadorId);
            stmt.executeUpdate();
        }
    }

    // ===================== FIND BY ID =====================
    @Override
    public Optional<Organizador> findById(Long id) {
        String sql = "SELECT * FROM organizador WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Organizador org = mapOrganizador(rs);
                    carregarEmpresa(conn, org);
                    return Optional.of(org);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar organizador por ID.");
        }
        return Optional.empty();
    }

    private Organizador mapOrganizador(ResultSet rs) throws SQLException {
        Organizador org = new Organizador();
        org.setId(rs.getLong("id"));
        org.setNome(rs.getString("nome"));
        org.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        org.setSexo(rs.getString("sexo"));
        org.setEmail(rs.getString("email"));
        org.setSenha(rs.getString("senha"));
        org.setAtivo(rs.getBoolean("ativo"));
        return org;
    }

    private void carregarEmpresa(Connection conn, Organizador org) throws SQLException {
        String sql = "SELECT cnpj, razao_social, nome_fantasia FROM empresa WHERE organizador_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, org.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rs.getString("cnpj"));
                    empresa.setRazaoSocial(rs.getString("razao_social"));
                    empresa.setNomeFantasia(rs.getString("nome_fantasia"));
                    empresa.setOrganizadorId(org.getId());
                    org.setEmpresa(empresa);
                }
            }
        }
    }

    // ===================== FIND WITH EVENTOS =====================
    public Optional<Organizador> findByIdWithEventos(Long id) {
        String sql = """
            SELECT o.id, o.nome, o.data_nascimento, o.sexo, o.email, o.senha, o.ativo,
                   e.id AS evento_id, e.organizador_id, e.nome AS evento_nome,
                   e.descricao, e.pagina_web, e.data_inicio, e.data_fim,
                   e.tipo_evento, e.modalidade, e.preco_unitario_ingresso,
                   e.taxa_cancelamento, e.evento_estorno, e.capacidade_maxima,
                   e.ingressos_disponiveis, e.local_evento, e.evento_ativo,
                   e.evento_principal_id
            FROM organizador o
            LEFT JOIN evento e ON o.id = e.organizador_id
            WHERE o.id = ?
            """;
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                Organizador organizador = null;
                List<Evento> eventos = new ArrayList<>();

                while (rs.next()) {
                    if (organizador == null) {
                        organizador = mapOrganizador(rs);
                        carregarEmpresa(conn, organizador);
                    }

                    long eventoId = rs.getLong("evento_id");
                    if (!rs.wasNull()) {
                        Evento evento = mapEvento(rs);
                        evento.setOrganizador(organizador);
                        long idPrincipal = rs.getLong("evento_principal_id");
                        if (!rs.wasNull()) {
                            Evento principal = new Evento();
                            principal.setId(idPrincipal);
                            evento.setEventoPrincipal(principal);
                        }
                        eventos.add(evento);
                    }
                }
                if (organizador != null) {
                    organizador.setEventos(eventos);
                    return Optional.of(organizador);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar organizador com eventos.");
        }
        return Optional.empty();
    }

    private Evento mapEvento(ResultSet rs) throws SQLException {
        String[] rowEvento = {
                rs.getString("evento_id"),
                rs.getString("organizador_id"),
                rs.getString("evento_nome"),
                rs.getString("descricao"),
                rs.getString("pagina_web"),
                rs.getString("data_inicio"),
                rs.getString("data_fim"),
                rs.getString("tipo_evento"),
                rs.getString("modalidade"),
                rs.getString("preco_unitario_ingresso"),
                rs.getString("taxa_cancelamento"),
                rs.getString("evento_estorno"),
                rs.getString("capacidade_maxima"),
                rs.getString("ingressos_disponiveis"),
                rs.getString("local_evento"),
                rs.getString("evento_ativo"),
                rs.getString("evento_principal_id")
        };
        return eventoMapper.mapRow(rowEvento);
    }

    // ===================== FIND ALL =====================
    @Override
    public Iterable<Organizador> findAll() {
        String sql = "SELECT * FROM organizador";
        List<Organizador> organizadores = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Organizador org = mapOrganizador(rs);
                carregarEmpresa(conn, org);
                organizadores.add(org);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar organizadores.");
        }
        return organizadores;
    }

    // ===================== DELETE =====================
    @Override
    public void deleteById(Long id) {
        // Como a empresa possui FK para organizador com ON DELETE RESTRICT, a ordem importa.
        // Primeiro deleta a empresa (se existir), depois o organizador.
        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false);
            try {
                deletarEmpresa(conn, id);
                String sql = "DELETE FROM organizador WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, id);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new PersistenciaException("Erro ao deletar organizador.");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro de conexão ao deletar organizador.");
        }
    }

    @Override
    public void delete(Organizador entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    // ===================== FIND BY FIELD (e-mail) =====================
    @Override
    public <V> Optional<Organizador> findByField(String fieldName, V value) {
        if (!"email".equals(fieldName)) {
            throw new UnsupportedOperationException("Busca por " + fieldName + " não suportada.");
        }
        String sql = "SELECT * FROM organizador WHERE email = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Organizador org = mapOrganizador(rs);
                    carregarEmpresa(conn, org);
                    return Optional.of(org);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao buscar organizador por email.");
        }
        return Optional.empty();
    }

    // ===================== EXISTS BY ID =====================
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM organizador WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao verificar existência de organizador.");
        }
    }

    // ===================== MÉTODOS NÃO IMPLEMENTADOS =====================
    @Override
    public long count() { throw new UnsupportedOperationException(); }
    @Override
    public Iterable<Organizador> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll(Iterable<? extends Organizador> entities) { throw new UnsupportedOperationException(); }
    @Override
    public void deleteAll() { throw new UnsupportedOperationException(); }
}