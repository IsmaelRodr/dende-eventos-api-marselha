package br.com.softhouse.dende.repositories;

import java.util.HashMap;
import java.util.Map;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.Organizador;

public class Repositorio {

    // 1. Padrão Singleton (A arquitetura que os seus colegas usaram)
    private static Repositorio instance = new Repositorio();

    // As listas guardam os utilizadores usando o ID numérico (Long)
    private Map<Long, Usuario> usuariosComum;
    private Map<Long, Organizador> organizadores;

    // 2. Construtor privado para o Singleton
    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
    }

    // 3. O famigerado método getInstance() que estava a dar erro!
    public static Repositorio getInstance() {
        return instance;
    }

    // --- AS NOSSAS FUNÇÕES INTACTAS (Com Long e regra de E-mail) ---

    public void salvarUsuario(Usuario usuario) {
        usuariosComum.put(usuario.getId(), usuario);
    }

    public void salvarOrganizador(Organizador organizador) {
        organizadores.put(organizador.getId(), organizador);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuariosComum.get(id);
    }

    public Organizador buscarOrganizadorPorId(Long id) {
        return organizadores.get(id);
    }
    
    public boolean emailExiste(String email) {
        for (Usuario u : usuariosComum.values()) {
            if (u.getEmail().equals(email)) return true;
        }
        for (Organizador o : organizadores.values()) {
            if (o.getEmail().equals(email)) return true;
        }
        return false; 
    }

    // --- MÉTODOS DE ATUALIZAR COMPLETOS (Pedido do Líder) ---
    
    public void atualizarDadosUsuario(Usuario usuarioExistente, Usuario novosDados) {
        usuarioExistente.setNome(novosDados.getNome());
        usuarioExistente.setDataNascimento(novosDados.getDataNascimento());
        usuarioExistente.setSexo(novosDados.getSexo());
        usuarioExistente.setSenha(novosDados.getSenha());
        
        usuariosComum.put(usuarioExistente.getId(), usuarioExistente); 
    }

    public void atualizarDadosOrganizador(Organizador orgExistente, Organizador novosDados) {
        orgExistente.setNome(novosDados.getNome());
        orgExistente.setDataNascimento(novosDados.getDataNascimento());
        orgExistente.setSexo(novosDados.getSexo());
        orgExistente.setSenha(novosDados.getSenha());
        orgExistente.setEmpresa(novosDados.getEmpresa()); // Nova classe Empresa
        
        organizadores.put(orgExistente.getId(), orgExistente); 
    }
}