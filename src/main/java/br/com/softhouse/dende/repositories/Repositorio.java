package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.util.HashMap;
import java.util.Map;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuariosComum;
    private final Map<String, Organizador> organizadores;

    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    // Métodos novos paras minhas APIs 01, 02 e 03

    // Salva ou atualiza um Usuário Comum usando o ID
    public void salvarUsuario(Usuario usuario) {
        usuariosComum.put(usuario.getId(), usuario);
    }

    // Salva ou atualiza um Organizador usando o ID
    public void salvarOrganizador(Organizador organizador) {
        organizadores.put(organizador.getId(), organizador);
    }

    // Busca um usuário específico pelo ID
    public Usuario buscarUsuarioPorId(String id) {
        return usuariosComum.get(id);
    }

    // Busca um organizador específico pelo ID
    public Organizador buscarOrganizadorPorId(String id) {
        return organizadores.get(id);
    }
    
    // Verifica se o e-mail já existe em QUALQUER UMA das listas (Regra de Negócio)
    public boolean emailExiste(String email) {
        // Procura na lista de usuários comuns
        for (Usuario u : usuariosComum.values()) {
            if (u.getEmail().equals(email)) {
                return true;
            }
        }
        // Procura na lista de organizadores
        for (Organizador o : organizadores.values()) {
            if (o.getEmail().equals(email)) {
                return true;
            }
        }
        return false; // Se não achou em nenhuma das duas, o e-mail está liberado!
    }
}

