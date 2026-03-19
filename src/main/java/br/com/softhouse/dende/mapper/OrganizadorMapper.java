package br.com.softhouse.dende.mapper;

import br.com.softhouse.dende.dto.organizador.AtualizarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.CadastrarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.EmpresaDto;
import br.com.softhouse.dende.dto.organizador.StatusOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.VisualizarOrganizadorDto;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;

public class OrganizadorMapper {

    public static Organizador toModel(CadastrarOrganizadorDto dto) {
        if (dto == null) return null;

        Organizador organizador = new Organizador();
        organizador.setNome(dto.nome());
        organizador.setDataNascimento(dto.dataNascimento());
        organizador.setSexo(dto.sexo());
        organizador.setEmail(dto.email());
        organizador.setSenha(dto.senha());
        organizador.setAtivo(dto.ativo());
        organizador.setEmpresa(toEmpresa(dto.empresa()));
        return organizador;
    }

    public static Organizador toModel(AtualizarOrganizadorDto dto) {
        if (dto == null) return null;

        Organizador organizador = new Organizador();
        organizador.setNome(dto.nome());
        organizador.setDataNascimento(dto.dataNascimento());
        organizador.setSexo(dto.sexo());
        organizador.setSenha(dto.senha());
        organizador.setEmpresa(toEmpresa(dto.empresa()));
        return organizador;
    }

    public static VisualizarOrganizadorDto toVisualizarDto(Organizador organizador) {
        if (organizador == null) return null;

        return new VisualizarOrganizadorDto(
                organizador.getNome(),
                organizador.getDataNascimento(),
                organizador.getSexo(),
                organizador.getEmail(),
                organizador.getSenha(),
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
}
