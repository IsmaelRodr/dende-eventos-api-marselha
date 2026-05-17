package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.mappers.EventoRowMapper;
import br.com.softhouse.dende.repositories.mappers.OrganizadorRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository; // O import com dois R's!

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizadorRepository implements CrudRepository<Organizador, Long> {

    private final OrganizadorRowMapper mapper = new OrganizadorRowMapper();
    private final EventoRowMapper eventoMapper = new EventoRowMapper();

    @Override
    public Organizador save(Organizador org) {
        String sql = "INSERT INTO organizadores (nome, data_nascimento, sexo, email, senha, ativo, cnpj, razao_social, nome_fantasia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, org.getNome());
            stmt.setDate(2, Date.valueOf(org.getDataNascimento()));
            stmt.setString(3, org.getSexo());
            stmt.setString(4, org.getEmail());
            stmt.setString(5, org.getSenha());
            stmt.setBoolean(6, org.isAtivo());
            stmt.setString(7, org.getEmpresa() != null ? org.getEmpresa().getCnpj() : null);
            stmt.setString(8, org.getEmpresa() != null ? org.getEmpresa().getRazaoSocial() : null);
            stmt.setString(9, org.getEmpresa() != null ? org.getEmpresa().getNomeFantasia() : null);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        org.setId(rs.getLong(1));
                    }
                }
            }
            return org;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar organizador.", e);
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
            throw new RuntimeException("Erro ao buscar organizador por ID.", e);
        }
        return Optional.empty();
    }

    public Optional<Organizador> findByIdWithEventos(Long id) {
        String sql = """
            SELECT o.id, o.nome, o.data_nascimento, o.sexo, o.email, o.senha,
                   o.ativo, o.cnpj, o.razao_social, o.nome_fantasia,
                   e.id AS evento_id, e.organizador_id, e.nome AS evento_nome,
                   e.descricao, e.pagina_web, e.data_inicio, e.data_fim,
                   e.tipo_evento, e.modalidade, e.preco_unitario,
                   e.taxa_cancelamento, e.evento_estorno, e.capacidade_maxima,
                   e.ingressos_disponiveis, e.local_evento, e.evento_ativo,
                   e.evento_principal_id
            FROM organizadores o
            LEFT JOIN eventos e ON o.id = e.organizador_id
            WHERE o.id = ?
            """;

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                Organizador organizador = null;
                List<Evento> eventos = new ArrayList<>();

                while (rs.next()) {
                    if (organizador == null) {
                        // Mapeia o organizador apenas uma vez
                        String[] rowOrg = {
                                rs.getString("id"), rs.getString("nome"), rs.getString("data_nascimento"),
                                rs.getString("sexo"), rs.getString("email"), rs.getString("senha"),
                                rs.getString("ativo"), rs.getString("cnpj"), rs.getString("razao_social"),
                                rs.getString("nome_fantasia")
                        };
                        organizador = mapper.mapRow(rowOrg);
                    }

                    // Se houver evento (pode ser nulo no LEFT JOIN)
                    long eventoId = rs.getLong("evento_id");
                    if (!rs.wasNull()) {
                        String[] rowEvento = {
                                rs.getString("evento_id"),          // id
                                rs.getString("organizador_id"),     // organizador_id
                                rs.getString("evento_nome"),        // nome
                                rs.getString("descricao"),
                                rs.getString("pagina_web"),
                                rs.getString("data_inicio"),
                                rs.getString("data_fim"),
                                rs.getString("tipo_evento"),
                                rs.getString("modalidade"),
                                rs.getString("preco_unitario"),
                                rs.getString("taxa_cancelamento"),
                                rs.getString("evento_estorno"),
                                rs.getString("capacidade_maxima"),
                                rs.getString("ingressos_disponiveis"),
                                rs.getString("local_evento"),
                                rs.getString("evento_ativo"),
                                rs.getString("evento_principal_id")
                        };
                        Evento evento = eventoMapper.mapRow(rowEvento);
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
                    organizador.setEventos(eventos);  // popula a lista interna
                    return Optional.of(organizador);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar organizador com eventos.", e);
        }
        return Optional.empty();
    }


    @Override
    public Iterable<Organizador> findAll() {
        String sql = "SELECT * FROM organizadores";
        List<Organizador> organizadores = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] row = {
                        rs.getString("id"), rs.getString("nome"), rs.getString("data_nascimento"),
                        rs.getString("sexo"), rs.getString("email"), rs.getString("senha"),
                        rs.getString("ativo"), rs.getString("cnpj"), rs.getString("razao_social"),
                        rs.getString("nome_fantasia")
                };
                organizadores.add(mapper.mapRow(row));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar organizadores.", e);
        }
        return organizadores;
    }

    @Override
    public Organizador update(Organizador org) {
        String sql = "UPDATE organizadores SET nome = ?, data_nascimento = ?, sexo = ?, email = ?, senha = ?, ativo = ?, cnpj = ?, razao_social = ?, nome_fantasia = ? WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, org.getNome());
            stmt.setDate(2, Date.valueOf(org.getDataNascimento()));
            stmt.setString(3, org.getSexo());
            stmt.setString(4, org.getEmail());
            stmt.setString(5, org.getSenha());
            stmt.setBoolean(6, org.isAtivo());
            stmt.setString(7, org.getEmpresa() != null ? org.getEmpresa().getCnpj() : null);
            stmt.setString(8, org.getEmpresa() != null ? org.getEmpresa().getRazaoSocial() : null);
            stmt.setString(9, org.getEmpresa() != null ? org.getEmpresa().getNomeFantasia() : null);
            stmt.setLong(10, org.getId());

            stmt.executeUpdate();
            return org;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar organizador.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM organizadores WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar organizador.", e);
        }
    }

    @Override
    public void delete(Organizador entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    @Override
    public <V> Optional<Organizador> findByField(String fieldName, V value) {
        if (!"email".equals(fieldName)) {
            throw new UnsupportedOperationException("Busca por " + fieldName + " não suportada.");
        }
        String sql = "SELECT * FROM organizadores WHERE email = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value.toString());
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
            throw new RuntimeException("Erro ao buscar organizador por email.", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM organizadores WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de organizador.", e);
        }
    }

    // Métodos não implementados
    @Override
    public long count() {
        throw new UnsupportedOperationException("Método não implementado");
    }
    @Override
    public Iterable<Organizador> findAllById(Iterable<Long> ids) {
        throw new UnsupportedOperationException("Método não implementado");
    }
    @Override
    public void deleteAll(Iterable<? extends Organizador> entities) {
        throw new UnsupportedOperationException("Método não implementado");
    }
    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Método não implementado");
    }
}