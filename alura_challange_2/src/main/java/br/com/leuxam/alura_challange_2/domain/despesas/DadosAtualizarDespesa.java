package br.com.leuxam.alura_challange_2.domain.despesas;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizarDespesa(
		@NotBlank
		String descricao,
		@NotNull
		BigDecimal valor,
		@JsonFormat(pattern = "dd/MM/yyyy")
		@NotNull
		LocalDate data,
		Categoria categoria) {
	
}
