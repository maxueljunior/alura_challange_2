package br.com.leuxam.alura_challange_2.domain.resumo;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.leuxam.alura_challange_2.domain.despesas.DespesasRepository;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasRepository;

@Service
public class ResumoService {
	
	@Autowired
	private DespesasRepository despesasRepository;
	
	@Autowired
	private ReceitasRepository receitasRepository;

	public DetalhamentoResumo calcularResumo(Integer ano, Integer mes) {
		
		var totalDespesas = despesasRepository.calculateTotalDespesasByMesAndAno(ano, mes);
		if(totalDespesas == null) {
			totalDespesas = BigDecimal.ZERO;
		}
		
		var totalReceitas = receitasRepository.calculateTotalReceitasByMesAndAno(ano, mes);
		if(totalReceitas == null) {
			totalReceitas = BigDecimal.ZERO;
		}
		
		var totalCategoria = despesasRepository.calculateTotalAllCategorias(ano, mes);
		
		var saldoFinal = totalReceitas.subtract(totalDespesas);
		
		return new DetalhamentoResumo(totalReceitas,
				totalDespesas, saldoFinal, totalCategoria);
	}
	
	
}
