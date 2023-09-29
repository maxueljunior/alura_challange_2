package br.com.leuxam.alura_challange_2.infra.exceptions;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class TratadorDeErros {
	
	@ExceptionHandler(ValidacaoException.class)
	public ResponseEntity Error400(ValidacaoException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
	public ResponseEntity Error404(){
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity Error400(MethodArgumentNotValidException ex) {
		var errors = ex.getFieldErrors().stream().map(DadosTratamentoErros::new);
		return ResponseEntity.badRequest().body(errors);
	}
	
	public record DadosTratamentoErros(String field, String message) {
		public DadosTratamentoErros(FieldError error) {
			this(error.getField(), error.getDefaultMessage());
		}
	}
}
