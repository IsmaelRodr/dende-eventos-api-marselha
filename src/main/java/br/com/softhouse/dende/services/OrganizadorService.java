package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.LoginDto;
import br.com.softhouse.dende.dto.organizador.*;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.organizador.*;
import br.com.softhouse.dende.exceptions.usuario.*;
import br.com.softhouse.dende.mapper.OrganizadorMapper;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;

public class OrganizadorService {

    private final Repositorio repositorio = Repositorio.getInstance();

    public StatusOrganizadorDto cadastrar(CadastrarOrganizadorDto dto) {
        validarCadastro(dto);
        if (repositorio.emailExiste(dto.email())) {
            throw new EmailJaCadastradoException("Já existe um organizador com este email.");
        }

        Organizador organizador = OrganizadorMapper.toModel(dto);
        repositorio.salvarOrganizador(organizador);
        return OrganizadorMapper.toStatusDto("Organizador cadastrado com sucesso!", organizador);
    }

    public StatusOrganizadorDto atualizar(Long id, AtualizarOrganizadorDto dto) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(id)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        validarAtualizacao(dto);

        OrganizadorMapper.updateModel(organizador, dto);
        repositorio.salvarOrganizador(organizador);
        return OrganizadorMapper.toStatusDto("Organizador atualizado com sucesso!", organizador);
    }

    public VisualizarOrganizadorDto buscarPorId(Long id) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(id)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        return OrganizadorMapper.toVisualizarDto(organizador);
    }

    public StatusOrganizadorDto ativar(Long id, LoginDto dto) {
        if (dto == null) throw new DadosInvalidosException("Credenciais são obrigatórias.");
        Organizador organizador = repositorio.buscarOrganizadorPorId(id)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        if (!organizador.getEmail().equalsIgnoreCase(dto.email())) {
            throw new CredenciaisInvalidasException("Email não confere.");
        }
        if (!organizador.getSenha().equals(dto.senha())) {
            throw new CredenciaisInvalidasException("Senha inválida.");
        }
        if (organizador.isAtivo()) {
            throw new OrganizadorJaAtivoException("Organizador já está ativo.");
        }
        organizador.setAtivo(true);
        repositorio.salvarOrganizador(organizador);
        return OrganizadorMapper.toStatusDto("Organizador reativado com sucesso!", organizador);
    }

    public StatusOrganizadorDto desativar(Long id) {
        Organizador organizador = repositorio.buscarOrganizadorPorId(id)
                .orElseThrow(() -> new OrganizadorNaoEncontradoException("Organizador não encontrado."));
        if (!organizador.isAtivo()) {
            throw new OrganizadorJaInativoException("Organizador já está inativo.");
        }
        boolean temEventosAtivos = organizador.getEventos().stream().anyMatch(e -> e.isEventoAtivo());
        if (temEventosAtivos) {
            throw new OrganizadorComEventosAtivosException("Organizador possui eventos ativos.");
        }
        organizador.setAtivo(false);
        repositorio.salvarOrganizador(organizador);
        return OrganizadorMapper.toStatusDto("Organizador desativado com sucesso!", organizador);
    }

    private void validarCadastro(CadastrarOrganizadorDto dto) {
        if (dto == null) throw new DadosInvalidosException("Dados inválidos.");
        if (dto.nome() == null || dto.nome().isBlank())
            throw new DadosInvalidosException("Nome é obrigatório.");
        if (dto.email() == null || dto.email().isBlank())
            throw new DadosInvalidosException("Email é obrigatório.");
        if (dto.senha() == null || dto.senha().isBlank())
            throw new DadosInvalidosException("Senha é obrigatória.");
        if (dto.sexo() == null || dto.sexo().isBlank())
            throw new DadosInvalidosException("Sexo é obrigatório.");
        if (dto.dataNascimento() == null)
            throw new DataNascimentoInvalidaException("Data de nascimento é obrigatória.");
        if (dto.dataNascimento().isAfter(LocalDate.now()))
            throw new DataNascimentoInvalidaException("Data de nascimento inválida.");
        validarEmail(dto.email());
        validarEmpresa(dto.empresa());
    }

    private void validarAtualizacao(AtualizarOrganizadorDto dto) {
        if (dto == null) throw new DadosInvalidosException("Dados inválidos.");
        if (dto.nome() != null && dto.nome().isBlank())
            throw new DadosInvalidosException("Nome inválido.");
        if (dto.senha() != null && dto.senha().isBlank())
            throw new DadosInvalidosException("Senha inválida.");
        if (dto.sexo() != null && dto.sexo().isBlank())
            throw new DadosInvalidosException("Sexo inválido.");
        if (dto.dataNascimento() != null && dto.dataNascimento().isAfter(LocalDate.now()))
            throw new DataNascimentoInvalidaException("Data de nascimento inválida.");
        if (dto.empresa() != null) validarEmpresa(dto.empresa());
    }

    private void validarEmpresa(Empresa empresa) {
        if (empresa == null) return;
        if (empresa.getCnpj() == null || empresa.getCnpj().isBlank())
            throw new EmpresaInvalidaException("CNPJ obrigatório.");
        if (empresa.getRazaoSocial() == null || empresa.getRazaoSocial().isBlank())
            throw new EmpresaInvalidaException("Razão social obrigatória.");
        if (empresa.getNomeFantasia() == null || empresa.getNomeFantasia().isBlank())
            throw new EmpresaInvalidaException("Nome fantasia obrigatório.");
        String cnpjNumerico = empresa.getCnpj().replaceAll("\\D", "");
        if (cnpjNumerico.length() != 14) throw new CnpjInvalidoException("CNPJ inválido.");
    }

    private void validarEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new EmailInvalidoException("Formato de email inválido.");
        }
    }
}