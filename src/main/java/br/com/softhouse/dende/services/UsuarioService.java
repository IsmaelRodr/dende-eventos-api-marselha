package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.LoginDto;
import br.com.softhouse.dende.dto.usuario.*;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.exceptions.usuario.*;
import br.com.softhouse.dende.mapper.UsuarioMapper;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;

public class UsuarioService {

    private final Repositorio repositorio = Repositorio.getInstance();

    public StatusUsuarioDto cadastrar(CadastrarUsuarioDto dto) {
        validarCadastro(dto);
        if (repositorio.emailExiste(dto.email())) {
            throw new EmailJaCadastradoException("Já existe um usuário com este email.");
        }

        Usuario usuario = UsuarioMapper.toModel(dto);
        repositorio.salvarUsuario(usuario);
        return UsuarioMapper.toStatusDto("Usuário registrado com sucesso!", usuario);
    }

    public StatusUsuarioDto atualizar(Long id, AtualizarUsuarioDto dto) {
        Usuario usuario = repositorio.buscarUsuarioPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        validarAtualizacao(dto);

        UsuarioMapper.updateModel(usuario, dto);
        repositorio.salvarUsuario(usuario);
        return UsuarioMapper.toStatusDto("Perfil atualizado com sucesso!", usuario);
    }

    public VisualizarUsuarioDto buscarPorId(Long id) {
        Usuario usuario = repositorio.buscarUsuarioPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        return UsuarioMapper.toVisualizarDto(usuario);
    }

    public StatusUsuarioDto ativar(Long id, LoginDto dto) {
        if (dto == null) throw new DadosInvalidosException("Credenciais são obrigatórias.");
        Usuario usuario = repositorio.buscarUsuarioPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        if (!usuario.getEmail().equalsIgnoreCase(dto.email())) {
            throw new CredenciaisInvalidasException("Email não confere.");
        }
        if (!usuario.getSenha().equals(dto.senha())) {
            throw new CredenciaisInvalidasException("Senha inválida.");
        }
        if (usuario.isAtivo()) {
            throw new UsuarioJaAtivoException("Usuário já está ativo.");
        }
        usuario.setAtivo(true);
        repositorio.salvarUsuario(usuario);
        return UsuarioMapper.toStatusDto("Usuário reativado com sucesso!", usuario);
    }

    public StatusUsuarioDto desativar(Long id) {
        Usuario usuario = repositorio.buscarUsuarioPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
        if (!usuario.isAtivo()) {
            throw new UsuarioJaInativoException("Usuário já está inativo.");
        }
        usuario.setAtivo(false);
        repositorio.salvarUsuario(usuario);
        return UsuarioMapper.toStatusDto("Usuário desativado com sucesso!", usuario);
    }

    private void validarCadastro(CadastrarUsuarioDto dto) {
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
    }

    private void validarAtualizacao(AtualizarUsuarioDto dto) {
        if (dto == null) throw new DadosInvalidosException("Dados de atualização inválidos.");
        if (dto.nome() != null && dto.nome().isBlank())
            throw new DadosInvalidosException("Nome inválido.");
        if (dto.senha() != null && dto.senha().isBlank())
            throw new DadosInvalidosException("Senha inválida.");
        if (dto.sexo() != null && dto.sexo().isBlank())
            throw new DadosInvalidosException("Sexo inválido.");
        if (dto.dataNascimento() != null && dto.dataNascimento().isAfter(LocalDate.now()))
            throw new DataNascimentoInvalidaException("Data de nascimento inválida.");
    }

    private void validarEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new EmailInvalidoException("Formato de email inválido.");
        }
    }
}