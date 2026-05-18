package br.com.softhouse.dende.exceptions.organizador;

import br.com.softhouse.dende.exceptions.DendeException;

public class OrganizadorJaAtivoException extends DendeException {

    public OrganizadorJaAtivoException(String mensagem) {
        super(mensagem);
    }
}