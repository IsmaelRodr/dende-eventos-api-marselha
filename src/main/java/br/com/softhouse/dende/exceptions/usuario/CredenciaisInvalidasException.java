package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class CredenciaisInvalidasException extends DendeException {

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}