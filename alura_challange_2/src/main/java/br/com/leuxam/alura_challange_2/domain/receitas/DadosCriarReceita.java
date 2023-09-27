package br.com.leuxam.alura_challange_2.domain.receitas;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCriarReceita(
		@NotBlank
		String descricao,
		@NotNull
		BigDecimal valor,
		@NotNull
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate data) {

}
