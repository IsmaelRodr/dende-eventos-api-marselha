package br.com.softhouse.dende.exceptions.repository;

import br.com.softhouse.dende.exceptions.DendeException;

public class PersistenciaException extends DendeException {

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }
}