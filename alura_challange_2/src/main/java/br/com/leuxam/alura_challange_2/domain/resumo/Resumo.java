package br.com.leuxam.alura_challange_2.domain.resumo;

import java.math.BigDecimal;

import br.com.leuxam.alura_challange_2.domain.despesas.Categoria;

public record Resumo(
		Categoria categoria,
		BigDecimal valor) {
}
