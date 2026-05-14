package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoJaAtivoException extends DendeException {

    public EventoJaAtivoException(String mensagem) {
        super(mensagem);
    }
}