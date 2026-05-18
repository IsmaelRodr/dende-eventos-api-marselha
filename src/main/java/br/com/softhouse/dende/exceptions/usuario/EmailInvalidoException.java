package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class EmailInvalidoException extends DendeException {

    public EmailInvalidoException(String mensagem) {
        super(mensagem);
    }
}