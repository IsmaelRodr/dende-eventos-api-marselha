package br.com.softhouse.dende.exceptions.repository;

import br.com.softhouse.dende.exceptions.DendeException;

public class OperacaoRepositorioException extends DendeException {

    public OperacaoRepositorioException(String mensagem) {
        super(mensagem);
    }
}