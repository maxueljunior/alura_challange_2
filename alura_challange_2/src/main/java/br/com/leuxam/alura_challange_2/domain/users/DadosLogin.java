package br.com.leuxam.alura_challange_2.domain.users;

import jakarta.validation.constraints.NotBlank;

public record DadosLogin(
		@NotBlank
		String username,
		@NotBlank
		String password) {

}
