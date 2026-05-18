package br.com.softhouse.dende.exceptions.usuario;

import br.com.softhouse.dende.exceptions.DendeException;

public class UsuarioInativoException extends DendeException {

    public UsuarioInativoException(String mensagem) {
        super(mensagem);
    }
}