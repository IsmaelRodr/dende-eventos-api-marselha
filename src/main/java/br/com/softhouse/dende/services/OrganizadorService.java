package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.organizador.AtualizarOrganizadorDto;
import br.com.softhouse.dende.dto.organizador.CadastrarOrganizadorDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.organizador.CnpjInvalidoException;
import br.com.softhouse.dende.exceptions.organizador.EmpresaInvalidaException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorJaAtivoException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorJaInativoException;
import br.com.softhouse.dende.exceptions.organizador.OrganizadorNaoEncontradoException;

import br.com.softhouse.dende.exceptions.usuario.DataNascimentoInvalidaException;
import br.com.softhouse.dende.exceptions.usuario.EmailJaCadastradoException;
import br.com.softhouse.dende.exceptions.usuario.EmailInvalidoException;

import br.com.softhouse.dende.mapper.OrganizadorMapper;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;

import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;

public class OrganizadorService {

    private final Repositorio repositorio;

    public OrganizadorService() {
        this.repositorio = Repositorio.getInstance();
    }

    public Organizador cadastrar(CadastrarOrganizadorDto dto) {

        if (dto == null) {
            throw new DadosInvalidosException(
                    "Dados do organizador inválidos."
            );
        }

        Organizador organizador = OrganizadorMapper.toModel(dto);

        if (organizador == null) {
            throw new DadosInvalidosException(
                    "Falha ao converter dados do organizador."
            );
        }

        if (organizador.getNome() == null ||
                organizador.getNome().isBlank()) {

            throw new DadosInvalidosException(
                    "Nome é obrigatório."
            );
        }

        if (organizador.getEmail() == null ||
                organizador.getEmail().isBlank()) {

            throw new DadosInvalidosException(
                    "Email é obrigatório."
            );
        }

        if (organizador.getSenha() == null ||
                organizador.getSenha().isBlank()) {

            throw new DadosInvalidosException(
                    "Senha é obrigatória."
            );
        }

        if (organizador.getSexo() == null ||
                organizador.getSexo().isBlank()) {

            throw new DadosInvalidosException(
                    "Sexo é obrigatório."
            );
        }

        if (organizador.getDataNascimento() == null) {

            throw new DataNascimentoInvalidaException(
                    "Data de nascimento é obrigatória."
            );
        }

        if (organizador.getDataNascimento().isAfter(LocalDate.now())) {

            throw new DataNascimentoInvalidaException(
                    "Data de nascimento inválida."
            );
        }

        if (!organizador.getEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new EmailInvalidoException(
                    "Formato de email inválido."
            );
        }

        if (repositorio.emailExiste(organizador.getEmail())) {

            throw new EmailJaCadastradoException(
                    "Já existe um organizador cadastrado com este email."
            );
        }

        validarEmpresa(organizador.getEmpresa());

        repositorio.salvarOrganizador(organizador);

        return organizador;
    }

    public Organizador atualizar(
            Long id,
            AtualizarOrganizadorDto dto
    ) {

        if (id == null || id <= 0) {

            throw new DadosInvalidosException(
                    "ID inválido."
            );
        }

        Organizador organizadorExistente =
                repositorio.buscarOrganizadorPorId(id);

        if (organizadorExistente == null) {

            throw new OrganizadorNaoEncontradoException(
                    "Organizador não encontrado."
            );
        }

        if (dto == null) {

            throw new DadosInvalidosException(
                    "Dados para atualização inválidos."
            );
        }

        Organizador organizadorAtualizado =
                OrganizadorMapper.toModel(dto);

        if (organizadorAtualizado == null) {

            throw new DadosInvalidosException(
                    "Falha ao converter dados."
            );
        }

        if (organizadorAtualizado.getNome() != null &&
                organizadorAtualizado.getNome().isBlank()) {

            throw new DadosInvalidosException(
                    "Nome inválido."
            );
        }

        if (organizadorAtualizado.getSenha() != null &&
                organizadorAtualizado.getSenha().isBlank()) {

            throw new DadosInvalidosException(
                    "Senha inválida."
            );
        }

        if (organizadorAtualizado.getSexo() != null &&
                organizadorAtualizado.getSexo().isBlank()) {

            throw new DadosInvalidosException(
                    "Sexo inválido."
            );
        }

        if (organizadorAtualizado.getDataNascimento() != null &&
                organizadorAtualizado.getDataNascimento()
                        .isAfter(LocalDate.now())) {

            throw new DataNascimentoInvalidaException(
                    "Data de nascimento inválida."
            );
        }

        validarEmpresa(organizadorAtualizado.getEmpresa());

        repositorio.atualizarDadosOrganizador(
                organizadorExistente,
                organizadorAtualizado
        );

        return organizadorExistente;
    }

    public Organizador buscarPorId(Long id) {

        if (id == null || id <= 0) {

            throw new DadosInvalidosException(
                    "ID inválido."
            );
        }

        Organizador organizador =
                repositorio.buscarOrganizadorPorId(id);

        if (organizador == null) {

            throw new OrganizadorNaoEncontradoException(
                    "Organizador não encontrado."
            );
        }

        return organizador;
    }

    public void ativar(Long id) {

        Organizador organizador = buscarPorId(id);

        if (organizador.isAtivo()) {

            throw new OrganizadorJaAtivoException(
                    "Organizador já está ativo."
            );
        }

        organizador.setAtivo(true);

        repositorio.salvarOrganizador(organizador);
    }

    public void desativar(Long id) {

        Organizador organizador = buscarPorId(id);

        if (!organizador.isAtivo()) {

            throw new OrganizadorJaInativoException(
                    "Organizador já está inativo."
            );
        }

        organizador.setAtivo(false);

        repositorio.salvarOrganizador(organizador);
    }

    private void validarEmpresa(Empresa empresa) {

        if (empresa == null) {
            return;
        }

        if (empresa.getCnpj() == null ||
                empresa.getCnpj().isBlank()) {

            throw new EmpresaInvalidaException(
                    "CNPJ é obrigatório."
            );
        }

        if (empresa.getRazaoSocial() == null ||
                empresa.getRazaoSocial().isBlank()) {

            throw new EmpresaInvalidaException(
                    "Razão social é obrigatória."
            );
        }

        if (empresa.getNomeFantasia() == null ||
                empresa.getNomeFantasia().isBlank()) {

            throw new EmpresaInvalidaException(
                    "Nome fantasia é obrigatório."
            );
        }

        String cnpjNumerico =
                empresa.getCnpj().replaceAll("\\D", "");

        if (cnpjNumerico.length() != 14) {

            throw new CnpjInvalidoException(
                    "CNPJ inválido."
            );
        }
    }
}