package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class EmailJaCadastradoException extends DendeException {

    public EmailJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}