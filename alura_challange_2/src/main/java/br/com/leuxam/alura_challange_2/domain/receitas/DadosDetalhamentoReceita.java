package br.com.leuxam.alura_challange_2.domain.receitas;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosDetalhamentoReceita(
		Long id,
		String descricao,
		BigDecimal valor,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate data) {
	
	public DadosDetalhamentoReceita(Receitas receita) {
		this(receita.getId(), receita.getDescricao(), receita.getValor(), receita.getData());
	}
}
