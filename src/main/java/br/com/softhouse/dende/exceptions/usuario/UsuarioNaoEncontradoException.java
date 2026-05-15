package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class UsuarioNaoEncontradoException extends DendeException {

    public UsuarioNaoEncontradoException(String s) {
        super("Usuário não encontrado.");
    }
}