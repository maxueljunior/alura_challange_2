package br.com.leuxam.alura_challange_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.leuxam.alura_challange_2.domain.despesas.DadosAtualizarDespesa;
import br.com.leuxam.alura_challange_2.domain.despesas.DadosCriarDespesa;
import br.com.leuxam.alura_challange_2.domain.despesas.DadosDetalhamentoDespesas;
import br.com.leuxam.alura_challange_2.domain.despesas.DespesasService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/despesas")
public class DespesasController {
	
	@Autowired
	private DespesasService service;
	
	@PostMapping
	public ResponseEntity<DadosDetalhamentoDespesas> save(
			@RequestBody @Valid DadosCriarDespesa dados,
			UriComponentsBuilder uriBuilder){
		var despesa = service.save(dados);
		var uri = uriBuilder.path("/despesas/{id}").buildAndExpand(despesa.id()).toUri();
		return ResponseEntity.created(uri).body(despesa);
	}
	
	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoDespesas>> findAll(
			@PageableDefault(size = 5, sort = {"descricao"}) Pageable pageable,
			@RequestParam(name = "descricao", defaultValue = "") String descricao){
		var despesas = service.findAll(pageable, descricao);
		return ResponseEntity.ok().body(despesas);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoDespesas> findById(
			@PathVariable(name = "id") Long id){
		var despesa = service.findById(id);
		return ResponseEntity.ok().body(despesa);
	}
	
	@GetMapping("/{ano}/{mes}")
	public ResponseEntity<Page<DadosDetalhamentoDespesas>> findAllByMesAndAno(
			@PathVariable(name = "ano") Integer ano,
			@PathVariable(name = "mes") Integer mes,
			@PageableDefault(size = 5, sort = {"descricao"}) Pageable pageable){
		var despesas = service.findAllByMesAndAno(ano, mes, pageable);
		return ResponseEntity.ok().body(despesas);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoDespesas> update(
			@PathVariable(name = "id") Long id,
			@RequestBody @Valid DadosAtualizarDespesa dados){
		var despesa = service.update(id, dados);
		return ResponseEntity.ok().body(despesa);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable(name = "id") Long id) {
		service.deletar(id);
		return ResponseEntity.noContent().build();
	}
}

















