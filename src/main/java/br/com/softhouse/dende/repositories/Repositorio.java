package br.com.softhouse.dende.repositories;

import java.util.HashMap;
import java.util.Map;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.Organizador;

public class Repositorio {

    // As listas agora guardam os usuários usando o ID numérico (Long)
    private Map<Long, Usuario> usuariosComum = new HashMap<>();
    private Map<Long, Organizador> organizadores = new HashMap<>();

    // --- AS SUAS FUNÇÕES INTACTAS (Apenas com Long no lugar de String) ---

    // Salva ou atualiza um Usuário Comum usando o ID
   public void salvarUsuario(Usuario usuario) {
        // Forçamos o ID a ser Long e garantimos que ele seja único (já é garantido pelo contadorIds na classe Usuario)
        usuariosComum.put(usuario.getId(), usuario);
    }

    public void salvarOrganizador(Organizador organizador) {
        organizadores.put(organizador.getId(), organizador);
    }

    // Busca um usuário específico pelo ID (AGORA É LONG)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuariosComum.get(id);
    }

    // Busca um organizador específico pelo ID (AGORA É LONG)
    public Organizador buscarOrganizadorPorId(Long id) {
        return organizadores.get(id);
    }
    
    // Verifica se o e-mail já existe em QUALQUER UMA das listas (Regra de Negócio INTACTA)
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

    // --- FUNÇÕES NOVAS PEDIDAS PELO LÍDER (Para a API 03 de Atualizar) ---
    
    public void atualizarDadosUsuario(Usuario usuarioExistente, Usuario novosDados) {
        usuarioExistente.setNome(novosDados.getNome());
        usuarioExistente.setSenha(novosDados.getSenha());
        usuariosComum.put(usuarioExistente.getId(), usuarioExistente); // Salva a atualização
    }

    public void atualizarDadosOrganizador(Organizador orgExistente, Organizador novosDados) {
        orgExistente.setNome(novosDados.getNome());
        orgExistente.setSenha(novosDados.getSenha());
        orgExistente.setRazaoSocial(novosDados.getRazaoSocial());
        orgExistente.setNomeFantasia(novosDados.getNomeFantasia());
        organizadores.put(orgExistente.getId(), orgExistente); // Salva a atualização
    }
}