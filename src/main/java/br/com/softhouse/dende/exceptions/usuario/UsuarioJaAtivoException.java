package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class UsuarioJaAtivoException extends DendeException {

    public UsuarioJaAtivoException(String mensagem) {
        super(mensagem);
    }
}