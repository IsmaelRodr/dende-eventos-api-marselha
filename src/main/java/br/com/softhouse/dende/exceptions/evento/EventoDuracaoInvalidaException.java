package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoDuracaoInvalidaException extends DendeException {

    public EventoDuracaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}