package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.usuario.*;
import br.com.softhouse.dende.model.Usuario;

public class UsuarioMapper {

    public static Usuario toModel(CadastrarUsuarioDto dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setSexo(dto.sexo());
        usuario.setEmail(dto.email()); 
        usuario.setSenha(dto.senha());
        return usuario;
    }

    public static Usuario toModel(AtualizarUsuarioDto dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setSexo(dto.sexo());
        usuario.setSenha(dto.senha());
        return usuario;
    }

    public static VisualizarUsuarioDto toVisualizarDto(Usuario usuario) {
        if (usuario == null) return null;
        return new VisualizarUsuarioDto(
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.getSexo(),
                usuario.getEmail()
        );
    }
}