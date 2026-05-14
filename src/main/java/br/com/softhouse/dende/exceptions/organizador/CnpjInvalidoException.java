package br.com.softhouse.dende.exceptions.organizador;

import br.com.softhouse.dende.exceptions.DendeException;

public class CnpjInvalidoException extends DendeException {

    public CnpjInvalidoException(String mensagem) {
        super(mensagem);
    }
}