package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.organizador.AtualizarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.CadastrarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.EmpresaDto;
import br.com.softhouse.dende.dto.organizador.StatusOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.VisualizarOrganizadorDto;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;

import java.time.LocalDate;
import java.time.Period;

public class OrganizadorMapper {

    public static Organizador toModel(CadastrarOrganizadorDto dto) {
        if (dto == null) return null;

        return new Organizador(
                null,
                dto.nome(),
                dto.dataNascimento(),
                dto.sexo(),
                dto.email(),
                dto.senha(),
                toEmpresa(dto.empresa())
        );
    }

    public static Organizador updateModel(Organizador organizador, AtualizarOrganizadorDto dto) {
        if (dto == null) return null;

        if (dto.nome() != null){organizador.setNome(dto.nome());}
        if (dto.dataNascimento()  != null) {organizador.setDataNascimento(dto.dataNascimento());}
        if (dto.sexo() != null){organizador.setSexo(dto.sexo());}
        if (dto.senha() != null){organizador.setSenha(dto.senha());}
        if (dto.empresa() != null){organizador.setEmpresa(toEmpresa(dto.empresa()));}
        return organizador;
    }

    public static VisualizarOrganizadorDto toVisualizarDto(Organizador organizador) {
        if (organizador == null) return null;

        return new VisualizarOrganizadorDto(
                organizador.getNome(),
                organizador.getDataNascimento(),
                calcularIdade(organizador.getDataNascimento()),
                organizador.getSexo(),
                organizador.getEmail(),
                organizador.isAtivo(),
                toEmpresaDto(organizador.getEmpresa())
        );
    }

    public static StatusOrganizadorDto toStatusDto(String mensagem, Organizador organizador) {
        if (organizador == null) return null;

        return new StatusOrganizadorDto(
                mensagem,
                organizador.getId(),
                organizador.isAtivo()
        );
    }

    private static Empresa toEmpresa(Empresa empresa) {
        if (empresa == null) return null;

        Empresa empresaModel = new Empresa();
        empresaModel.setCnpj(empresa.getCnpj());
        empresaModel.setRazaoSocial(empresa.getRazaoSocial());
        empresaModel.setNomeFantasia(empresa.getNomeFantasia());
        return empresaModel;
    }

    private static EmpresaDto toEmpresaDto(Empresa empresa) {
        if (empresa == null) return null;

        return new EmpresaDto(
                empresa.getCnpj(),
                empresa.getRazaoSocial(),
                empresa.getNomeFantasia()
        );
    }

    private static String calcularIdade(LocalDate nascimento) {
        Period p = Period.between(nascimento, LocalDate.now());
        return p.getYears() + " anos, " +
                p.getMonths() + " meses, " +
                p.getDays() + " dias";
    }
}
