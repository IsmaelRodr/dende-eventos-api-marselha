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

    //1. Vai verificar se o email já existe em algum dos mapas (usuariosComum ou organizadores)
    public boolean emailExiste(String email) 
    {
        return usuariosComum.containsKey(email) || organizadores.containsKey(email);
    }

    //2. Vai salavar ou atualizar um usuário comum
    public void salvarUsuarioComum(Usuario usuario) 
    {
        usuariosComum.put(usuario.getEmail(), usuario);
    }

    //3. Vai salvar ou atualizar um organizador
    public void salvarOrganizador(Organizador organizador) {
        organizadores.put(organizador.getEmail(), organizador);
    }

    //4. Vai buscar um usuário comum pelo email
    public Usuario buscarUsuario(String email) {
        return usuariosComum.get(email);
    }

    //5. Vai buscar um organizador pelo email
    public Organizador buscarOrganizador(String email) {
        return organizadores.get(email);
    }

    // 6. Salva ou atualiza um Usuário Comum
    public void salvarUsuario(Usuario usuario) {
        usuariosComum.put(usuario.getEmail(), usuario);
    }
}

