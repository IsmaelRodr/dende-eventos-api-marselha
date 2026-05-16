package br.com.softhouse.dende.exceptions.repository;

import br.com.softhouse.dende.exceptions.DendeException;

public class EntidadeNaoEncontradaException extends DendeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}