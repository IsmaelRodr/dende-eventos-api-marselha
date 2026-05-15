package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoJaInativoException extends DendeException {

    public EventoJaInativoException(String mensagem) {
        super(mensagem);
    }
}