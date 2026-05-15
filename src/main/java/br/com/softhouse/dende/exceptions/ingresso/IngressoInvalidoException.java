package br.com.softhouse.dende.exceptions.ingresso;

import br.com.softhouse.dende.exceptions.DendeException;

public class IngressoInvalidoException extends DendeException {

    public IngressoInvalidoException(String mensagem) {
        super(mensagem);
    }
}