package br.com.softhouse.dende.exceptions.evento;

import br.com.softhouse.dende.exceptions.DendeException;

public class EventoExpiradoException extends DendeException {

  public EventoExpiradoException(String mensagem) {
    super(mensagem);
  }
}