package br.com.softhouse.dende.exceptions.ingresso;

import br.com.softhouse.dende.exceptions.DendeException;

public class EstornoNaoPermitidoException extends DendeException {

  public EstornoNaoPermitidoException(String mensagem) {
    super(mensagem);
  }
}