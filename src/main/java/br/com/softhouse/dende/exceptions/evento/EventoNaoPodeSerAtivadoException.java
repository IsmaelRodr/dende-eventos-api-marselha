package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoNaoPodeSerAtivadoException extends DendeException {

    public EventoNaoPodeSerAtivadoException(String mensagem) {
        super(mensagem);
    }
}