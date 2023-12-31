package br.com.leuxam.alura_challange_2.domain.resumo;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import br.com.leuxam.alura_challange_2.domain.despesas.DespesasRepository;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasRepository;

@Service
public class ResumoService {
	
	private DespesasRepository despesasRepository;
	
	private ReceitasRepository receitasRepository;
	
	@Autowired
	public ResumoService(DespesasRepository despesasRepository, ReceitasRepository receitasRepository) {
		this.despesasRepository = despesasRepository;
		this.receitasRepository = receitasRepository;
	}

	public DetalhamentoResumo calcularResumo(Integer ano, Integer mes) {
		
		if(mes > 12 || mes < 1) throw new ValidacaoException("O mês está invalido, porfavor digite um numero de 1 a 12");
		
		var totalDespesas = despesasRepository.calculateTotalDespesasByMesAndAno(ano, mes);
		if(totalDespesas == null) {
			totalDespesas = BigDecimal.ZERO;
		}
		
		var totalReceitas = receitasRepository.calculateTotalReceitasByMesAndAno(ano, mes);
		if(totalReceitas == null) {
			totalReceitas = BigDecimal.ZERO;
		}
		
		var totalCategoria = despesasRepository.calculateTotalAllCategorias(ano, mes);
		
		var saldoFinal = totalDespesas.subtract(totalReceitas);
		
		return new DetalhamentoResumo(totalReceitas,
				totalDespesas, saldoFinal, totalCategoria);
	}
	
	
}
