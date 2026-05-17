package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.mappers.UsuarioRowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.dende.softhouse.repositorry.CrudRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository implements CrudRepository<Usuario, Long> {

    private final UsuarioRowMapper mapper = new UsuarioRowMapper();

    @Override
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, data_nascimento, sexo, email, senha, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setBoolean(6, usuario.isAtivo());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getLong(1));
                    }
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o usuário no banco de dados.", e);
        }
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("nome"), rs.getString("data_nascimento"),
                            rs.getString("sexo"), rs.getString("email"), rs.getString("senha"), rs.getString("ativo")
                    };
                    return Optional.of(mapper.mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o usuário pelo ID.", e);
        }
        return Optional.empty(); // Retorna vazio se não achar, exatamente como o professor pediu
    }

    @Override
    public Iterable<Usuario> findAll() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] row = {
                        rs.getString("id"), rs.getString("nome"), rs.getString("data_nascimento"),
                        rs.getString("sexo"), rs.getString("email"), rs.getString("senha"), rs.getString("ativo")
                };
                usuarios.add(mapper.mapRow(row));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os usuários.", e);
        }
        return usuarios;
    }

    @Override
    public Usuario update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, data_nascimento = ?, sexo = ?, email = ?, senha = ?, ativo = ? WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setBoolean(6, usuario.isAtivo());
            stmt.setLong(7, usuario.getId());

            stmt.executeUpdate();
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o usuário.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o usuário.", e);
        }
    }

    @Override
    public void delete(Usuario entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    @Override
    public <V> Optional<Usuario> findByField(String fieldName, V value) {
        if (!"email".equals(fieldName)) {
            throw new UnsupportedOperationException("Busca por " + fieldName + " não suportada.");
        }
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String[] row = {
                            rs.getString("id"), rs.getString("nome"), rs.getString("data_nascimento"),
                            rs.getString("sexo"), rs.getString("email"), rs.getString("senha"), rs.getString("ativo")
                    };
                    return Optional.of(mapper.mapRow(row));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por email.", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de usuário.", e);
        }
    }

    // ==============================================================================
    // MÉTODOS OBRIGATÓRIOS DA INTERFACE (Ainda não implementados na regra de negócio)
    // ==============================================================================

    @Override
    public long count() { throw new UnsupportedOperationException("Método não implementado"); }

    @Override
    public Iterable<Usuario> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException("Método não implementado"); }

    @Override
    public void deleteAll(Iterable<? extends Usuario> entities) { throw new UnsupportedOperationException("Método não implementado"); }

    @Override
    public void deleteAll() { throw new UnsupportedOperationException("Método não implementado"); }
}