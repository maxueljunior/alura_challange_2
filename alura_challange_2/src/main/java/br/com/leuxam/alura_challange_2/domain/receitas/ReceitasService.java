package br.com.leuxam.alura_challange_2.domain.receitas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class ReceitasService {
	
	private ReceitasRepository receitasRepository;
	
	@Autowired
	public ReceitasService(ReceitasRepository receitasRepository) {
		this.receitasRepository = receitasRepository;
	}

	@Transactional
	public Receitas save(DadosCriarReceita dados) {

		var mes = dados.data().getMonthValue();
		var ano = dados.data().getYear();

		var existeAlgumaReceita = receitasRepository.existeAlgumaReceitasByDescricao(dados.descricao(),
				mes, ano);

		if(existeAlgumaReceita != null) {
			throw new ValidacaoException("Já existe uma receita " + dados.descricao() + " na data " + mes + "/" + ano);
		}
		
		var receita = new Receitas(dados);
		receitasRepository.save(receita);
		return receita;
	}

    public Page<DadosDetalhamentoReceita> findAll(Pageable pageable, String descricao) {
		var receitas = receitasRepository.findByDescricaoLike("%" + descricao + "%",pageable)
				.map(DadosDetalhamentoReceita::new);
		return receitas;
    }

	public DadosDetalhamentoReceita findById(Long id) {
		var receita = receitasRepository.getReferenceById(id);
		return new DadosDetalhamentoReceita(receita);
	}
	
	public Page<DadosDetalhamentoReceita> findAllByMesAndAno(Integer ano, Integer mes, 
			Pageable pageable) {
		if(mes > 12 || mes < 1) throw new ValidacaoException("O mês está invalido, porfavor digite um numero de 1 a 12");
		var receitas = receitasRepository.findByAnoAndMes(ano, mes, pageable)
				.map(DadosDetalhamentoReceita::new);
		return receitas;
	}

	@Transactional
	public DadosDetalhamentoReceita update(Long id, DadosAtualizarReceita dados) {
		var receita = receitasRepository.findById(id);
		
		var anoDados = dados.data().getYear();
		var anoReceita = receita.get().getData().getYear();
		
		var descReceita = receita.get().getDescricao();
		var descDados = dados.descricao();

		var mesReceita = receita.get().getData().getMonthValue();
		var mesDados = dados.data().getMonthValue();

		if(mesDados != mesReceita || !descDados.equalsIgnoreCase(descReceita) ||
				anoDados != anoReceita) {
			var existeAlgumaReceita = receitasRepository.existeAlgumaReceitasByDescricao(dados.descricao(),
					mesDados, anoDados);

			if (existeAlgumaReceita != null) {
				throw new ValidacaoException("Já existe uma receita " + descDados + " na data " + mesDados + "/" + anoDados);
			}
		}

		receita.get().atualizarDados(dados);

		return new DadosDetalhamentoReceita(receita.get());
	}
	
	@Transactional
	public void delete(Long id) {
		var receita = receitasRepository.findById(id);
		receitasRepository.delete(receita.get());
	}

	

}














