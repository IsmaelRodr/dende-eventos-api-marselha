package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.usuario.*;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;

import java.time.LocalDate;
import java.time.Period;

public class UsuarioMapper {

    public static Usuario toModel(CadastrarUsuarioDto dto) {
        if (dto == null) return null;
        return new Usuario(
                null,
                dto.nome(),
                dto.dataNascimento(),
                dto.sexo(),
                dto.email(),
                dto.senha());
    }

    public static void updateModel(Usuario usuario, AtualizarUsuarioDto dto) {
        if (dto == null || usuario == null) return;

        if (dto.nome() != null){usuario.setNome(dto.nome());}
        if (dto.dataNascimento()  != null) {usuario.setDataNascimento(dto.dataNascimento());}
        if (dto.sexo() != null){usuario.setSexo(dto.sexo());}
        if (dto.senha() != null){usuario.setSenha(dto.senha());}
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