package br.com.leuxam.alura_challange_2.domain.despesas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class DespesasService {
	
	@Autowired
	private DespesasRepository despesasRepository;
	
	@Transactional
	public DadosDetalhamentoDespesas save(DadosCriarDespesa dados) {
		var ano = dados.data().getYear();
		var mes = dados.data().getMonthValue();
		var descricao = dados.descricao();
		var existeAlgumaDespesa = despesasRepository.existeAlgumaReceitasByDescricao(
				descricao, mes, ano);
		
		if(existeAlgumaDespesa != null) throw new ValidacaoException("Já existe uma despesa " + descricao + " na data " + mes + "/" + ano);
		
		var despesa = new Despesas(dados);
		despesasRepository.save(despesa);
		return new DadosDetalhamentoDespesas(despesa);
	}
	
	public Page<DadosDetalhamentoDespesas> findAll(Pageable pageable, String descricao){
		var despesas = despesasRepository.findByDescricaoLike("%" + descricao + "%", pageable).map(DadosDetalhamentoDespesas::new);
		return despesas;
	}

	public DadosDetalhamentoDespesas findById(Long id) {
		var despesa = despesasRepository.getReferenceById(id);
		
		return new DadosDetalhamentoDespesas(despesa);
	}

	public Page<DadosDetalhamentoDespesas> findAllByMesAndAno(
			Integer ano, Integer mes, Pageable pageable) {
		if(mes > 12 || mes < 1) throw new ValidacaoException("O mês está invalido, porfavor digite um numero de 1 a 12");
		var despesas = despesasRepository.findAllByMesAndAno(ano, mes, pageable)
				.map(DadosDetalhamentoDespesas::new);
		return despesas;
	}

	@Transactional
	public DadosDetalhamentoDespesas update(Long id, DadosAtualizarDespesa dados) {
		
		var despesa = despesasRepository.findById(id).get();
		
		var anoDespesa = despesa.getData().getYear();
		var anoDados = dados.data().getYear();
		
		var descDespesa = despesa.getDescricao();
		var descDados = dados.descricao();
		
		var mesDespesa = despesa.getData().getMonthValue();
		var mesDados = dados.data().getMonthValue();
		
		if(mesDespesa != mesDados || !descDados.equalsIgnoreCase(descDespesa) ||
				anoDespesa != anoDados) {
			var existeAlgumaDespesa = despesasRepository
					.existeAlgumaReceitasByDescricao(descDados, mesDados, anoDados);
			
			if(existeAlgumaDespesa != null) {
				throw new ValidacaoException("Já existe uma despesa " + descDados + " na data " + mesDados + "/" + anoDados);
			}
		}
		
		despesa.atualizarDados(dados);
		
		return new DadosDetalhamentoDespesas(despesa);
	}
	
	@Transactional
	public void deletar(Long id) {
		var despesa = despesasRepository.findById(id);
		despesasRepository.delete(despesa.get());
	}
}
