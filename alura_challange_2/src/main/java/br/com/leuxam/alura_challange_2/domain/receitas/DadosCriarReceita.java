package br.com.leuxam.alura_challange_2.domain.receitas;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCriarReceita(
		@NotBlank
		String descricao,
		@NotNull
		BigDecimal valor,
		@NotNull
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate data) {

}
