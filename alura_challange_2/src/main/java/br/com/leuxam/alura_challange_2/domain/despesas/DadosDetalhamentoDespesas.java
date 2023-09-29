package br.com.leuxam.alura_challange_2.domain.despesas;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosDetalhamentoDespesas(
		Long id,
		String descricao,
		BigDecimal valor,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate data,
		Categoria categoria) {
	
	public DadosDetalhamentoDespesas(Despesas despesa) {
		this(despesa.getId(), despesa.getDescricao(), despesa.getValor(), despesa.getData(),
				despesa.getCategoria());
	}
}
