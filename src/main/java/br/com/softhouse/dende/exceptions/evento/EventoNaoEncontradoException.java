package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoNaoEncontradoException extends DendeException {

    public EventoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}