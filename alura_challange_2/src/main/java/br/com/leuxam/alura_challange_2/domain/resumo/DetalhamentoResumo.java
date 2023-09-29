package br.com.leuxam.alura_challange_2.domain.resumo;

import java.math.BigDecimal;
import java.util.List;

public record DetalhamentoResumo(
		BigDecimal valoTotalReceitas,
		BigDecimal valorTotalDespesas,
		BigDecimal saldoFinal,
		List<Resumo> totalCategorias) {
	
}
