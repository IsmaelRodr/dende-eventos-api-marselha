package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.usuario.*;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;

import java.time.LocalDate;
import java.time.Period;

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
                calcularIdade(usuario.getDataNascimento()),
                usuario.getSexo(),
                usuario.getEmail(),
                usuario.isAtivo()
        );
    }

    public static StatusUsuarioDto toStatusDto(String mensagem, Usuario usuario) {
        if (usuario == null) return null;

        return new StatusUsuarioDto(
                mensagem,
                usuario.getId(),
                usuario.isAtivo()
        );
    }

    public static CancelarIngressoUsuarioDto toCancelarDTO(String mensagem, Ingresso ingresso){
      if (ingresso == null) return null;

      return new CancelarIngressoUsuarioDto(
              mensagem,
              ingresso.getId(),
              ingresso.getValorEstornado()
      );
    }

    private static String calcularIdade(LocalDate nascimento) {
        Period p = Period.between(nascimento, LocalDate.now());
        return p.getYears() + " anos, " +
                p.getMonths() + " meses, " +
                p.getDays() + " dias";
    }
}