package br.com.softhouse.dende.exceptions.ingresso;

import br.com.softhouse.dende.exceptions.DendeException;

public class IngressoNaoEncontradoException extends DendeException {

    public IngressoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}