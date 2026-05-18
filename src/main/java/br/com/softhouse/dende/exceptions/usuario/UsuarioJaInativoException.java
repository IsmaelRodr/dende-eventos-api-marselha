package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class UsuarioJaInativoException extends DendeException {

    public UsuarioJaInativoException(String mensagem) {
        super(mensagem);
    }
}