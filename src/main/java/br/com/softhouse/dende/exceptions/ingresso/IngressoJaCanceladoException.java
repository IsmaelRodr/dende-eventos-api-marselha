package br.com.softhouse.dende.exceptions.ingresso;

import br.com.softhouse.dende.exceptions.DendeException;

public class IngressoJaCanceladoException extends DendeException {

    public IngressoJaCanceladoException(String mensagem) {
        super(mensagem);
    }
}