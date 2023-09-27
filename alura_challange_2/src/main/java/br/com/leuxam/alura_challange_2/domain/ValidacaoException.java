package br.com.leuxam.alura_challange_2.domain;

public class ValidacaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ValidacaoException(String msg) {
		super(msg);
	}
}
