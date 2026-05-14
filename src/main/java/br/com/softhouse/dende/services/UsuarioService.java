package br.com.softhouse.dende.services;

import br.com.softhouse.dende.dto.usuario.AtualizarUsuarioDto;
import br.com.softhouse.dende.dto.usuario.CadastrarUsuarioDto;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;

import br.com.softhouse.dende.exceptions.usuario.DataNascimentoInvalidaException;
import br.com.softhouse.dende.exceptions.usuario.EmailJaCadastradoException;
import br.com.softhouse.dende.exceptions.usuario.EmailInvalidoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioJaAtivoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioJaInativoException;
import br.com.softhouse.dende.exceptions.usuario.UsuarioNaoEncontradoException;

import br.com.softhouse.dende.mapper.UsuarioMapper;

import br.com.softhouse.dende.model.Usuario;

import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;

public class UsuarioService {

    private final Repositorio repositorio;

    public UsuarioService() {
        this.repositorio = Repositorio.getInstance();
    }

    public Usuario cadastrar(CadastrarUsuarioDto dto) {

        if (dto == null) {

            throw new DadosInvalidosException(
                    "Dados do usuário inválidos."
            );
        }

        Usuario usuario = UsuarioMapper.toModel(dto);

        if (usuario == null) {

            throw new DadosInvalidosException(
                    "Falha ao converter dados do usuário."
            );
        }

        validarUsuarioCadastro(usuario);

        if (repositorio.emailExiste(usuario.getEmail())) {

            throw new EmailJaCadastradoException(
                    "Já existe um usuário cadastrado com este email."
            );
        }

        repositorio.salvarUsuario(usuario);

        return usuario;
    }

    public Usuario atualizar(
            Long id,
            AtualizarUsuarioDto dto
    ) {

        if (id == null || id <= 0) {

            throw new DadosInvalidosException(
                    "ID inválido."
            );
        }

        Usuario usuarioExistente =
                repositorio.buscarUsuarioPorId(id);

        if (usuarioExistente == null) {

            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        if (dto == null) {

            throw new DadosInvalidosException(
                    "Dados para atualização inválidos."
            );
        }

        Usuario usuarioAtualizado =
                UsuarioMapper.toModel(dto);

        if (usuarioAtualizado == null) {

            throw new DadosInvalidosException(
                    "Falha ao converter dados."
            );
        }

        validarUsuarioAtualizacao(usuarioAtualizado);

        repositorio.atualizarDadosUsuario(
                usuarioExistente,
                usuarioAtualizado
        );

        return usuarioExistente;
    }

    public Usuario buscarPorId(Long id) {

        if (id == null || id <= 0) {

            throw new DadosInvalidosException(
                    "ID inválido."
            );
        }

        Usuario usuario =
                repositorio.buscarUsuarioPorId(id);

        if (usuario == null) {

            throw new UsuarioNaoEncontradoException(
                    "Usuário não encontrado."
            );
        }

        return usuario;
    }

    public void ativar(Long id) {

        Usuario usuario = buscarPorId(id);

        if (usuario.isAtivo()) {

            throw new UsuarioJaAtivoException(
                    "Usuário já está ativo."
            );
        }

        usuario.setAtivo(true);

        repositorio.salvarUsuario(usuario);
    }

    public void desativar(Long id) {

        Usuario usuario = buscarPorId(id);

        if (!usuario.isAtivo()) {

            throw new UsuarioJaInativoException(
                    "Usuário já está inativo."
            );
        }

        usuario.setAtivo(false);

        repositorio.salvarUsuario(usuario);
    }

    private void validarUsuarioCadastro(
            Usuario usuario
    ) {

        if (usuario.getNome() == null ||
                usuario.getNome().isBlank()) {

            throw new DadosInvalidosException(
                    "Nome é obrigatório."
            );
        }

        if (usuario.getEmail() == null ||
                usuario.getEmail().isBlank()) {

            throw new DadosInvalidosException(
                    "Email é obrigatório."
            );
        }

        if (usuario.getSenha() == null ||
                usuario.getSenha().isBlank()) {

            throw new DadosInvalidosException(
                    "Senha é obrigatória."
            );
        }

        if (usuario.getSexo() == null ||
                usuario.getSexo().isBlank()) {

            throw new DadosInvalidosException(
                    "Sexo é obrigatório."
            );
        }

        if (usuario.getDataNascimento() == null) {

            throw new DataNascimentoInvalidaException(
                    "Data de nascimento é obrigatória."
            );
        }

        if (usuario.getDataNascimento()
                .isAfter(LocalDate.now())) {

            throw new DataNascimentoInvalidaException(
                    "Data de nascimento inválida."
            );
        }

        validarEmail(usuario.getEmail());
    }

    private void validarUsuarioAtualizacao(
            Usuario usuario
    ) {

        if (usuario.getNome() != null &&
                usuario.getNome().isBlank()) {

            throw new DadosInvalidosException(
                    "Nome inválido."
            );
        }

        if (usuario.getSenha() != null &&
                usuario.getSenha().isBlank()) {

            throw new DadosInvalidosException(
                    "Senha inválida."
            );
        }

        if (usuario.getSexo() != null &&
                usuario.getSexo().isBlank()) {

            throw new DadosInvalidosException(
                    "Sexo inválido."
            );
        }

        if (usuario.getDataNascimento() != null &&
                usuario.getDataNascimento()
                        .isAfter(LocalDate.now())) {

            throw new DataNascimentoInvalidaException(
                    "Data de nascimento inválida."
            );
        }

        if (usuario.getEmail() != null) {

            validarEmail(usuario.getEmail());
        }
    }

    private void validarEmail(String email) {

        boolean emailValido =
                email.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                );

        if (!emailValido) {

            throw new EmailInvalidoException(
                    "Formato de email inválido."
            );
        }
    }
}