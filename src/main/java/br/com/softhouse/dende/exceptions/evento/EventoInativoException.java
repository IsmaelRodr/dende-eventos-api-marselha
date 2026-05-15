package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoInativoException extends DendeException {

    public EventoInativoException(String mensagem) {
        super(mensagem);
    }
}