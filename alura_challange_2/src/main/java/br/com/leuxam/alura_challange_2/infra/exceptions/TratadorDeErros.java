package br.com.leuxam.alura_challange_2.infra.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;

@RestControllerAdvice
public class TratadorDeErros {
	
	@ExceptionHandler(ValidacaoException.class)
	public ResponseEntity Error400(ValidacaoException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
