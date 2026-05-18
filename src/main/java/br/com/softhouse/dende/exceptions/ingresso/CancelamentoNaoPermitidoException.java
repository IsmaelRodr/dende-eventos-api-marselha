package br.com.softhouse.dende.exceptions.ingresso;

import br.com.softhouse.dende.exceptions.DendeException;

public class CancelamentoNaoPermitidoException extends DendeException {

    public CancelamentoNaoPermitidoException(String mensagem) {
        super(mensagem);
    }
}