package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class DataNascimentoInvalidaException extends DendeException {

    public DataNascimentoInvalidaException(String mensagem) {
        super(mensagem);
    }
}