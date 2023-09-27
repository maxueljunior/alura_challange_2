package br.com.leuxam.alura_challange_2.domain.receitas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class ReceitasService {
	
	@Autowired
	private ReceitasRepository receitasRepository;
	
	@Transactional
	public Receitas save(DadosCriarReceita dados) {
		
		var existeAlgumaReceita = receitasRepository.existeAlgumaReceitasByDescricao(
				dados.descricao(), dados.data().getMonthValue());
		
		if(existeAlgumaReceita != null) {
			throw new ValidacaoException("Já existe uma receita " + dados.descricao() + " no mês " + dados.data().getMonthValue());
		}
		
		var receita = new Receitas(dados);
		receitasRepository.save(receita);
		return receita;
	}
}
