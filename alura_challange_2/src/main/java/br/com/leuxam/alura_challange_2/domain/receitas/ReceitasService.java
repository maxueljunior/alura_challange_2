package br.com.leuxam.alura_challange_2.domain.receitas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class ReceitasService {
	
	@Autowired
	private ReceitasRepository receitasRepository;
	
	@Transactional
	public Receitas save(DadosCriarReceita dados) {

		var mes = dados.data().getMonthValue();

		var existeAlgumaReceita = existeAlgumaReceita(dados.descricao(),
				mes);

		if(existeAlgumaReceita != null) {
			throw new ValidacaoException("Já existe uma receita " + dados.descricao() + " no mês " + mes);
		}
		
		var receita = new Receitas(dados);
		receitasRepository.save(receita);
		return receita;
	}

    public Page<DadosDetalhamentoReceita> findAll(Pageable pageable) {
		var receitas = receitasRepository.findAll(pageable)
				.map(DadosDetalhamentoReceita::new);
		return receitas;
    }

	public DadosDetalhamentoReceita findById(Long id) {
		var receita = receitasRepository.getReferenceById(id);
		return new DadosDetalhamentoReceita(receita);
	}

	@Transactional
	public DadosDetalhamentoReceita update(Long id, DadosAtualizarReceita dados) {
		var receita = receitasRepository.findById(id).get();

		var descReceita = receita.getDescricao();
		var descDados = dados.descricao();

		var mesReceita = receita.getData().getMonthValue();
		var mesDados = dados.data().getMonthValue();

		if(mesDados != mesReceita || !descDados.equalsIgnoreCase(descReceita)) {
			var existeAlgumaReceita = existeAlgumaReceita(dados.descricao(),
					mesDados);

			if (existeAlgumaReceita != null) {
				throw new ValidacaoException("Já existe uma receita " + descDados + " no mês " + mesDados);
			}
		}

		receita.atualizarDados(dados);

		return new DadosDetalhamentoReceita(receita);
	}

	private Receitas existeAlgumaReceita(String descricao, int mes){
		var existeAlgumaReceita = receitasRepository.existeAlgumaReceitasByDescricao(
				descricao, mes);

		return existeAlgumaReceita;
	}
	
	@Transactional
	public void delete(Long id) {
		var receita = receitasRepository.findById(id);
		
		if(!receita.isPresent()) throw new ValidacaoException("Id " + id + " não encontrado");
		receitasRepository.delete(receita.get());
	}

}














