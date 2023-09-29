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

import br.com.leuxam.alura_challange_2.domain.receitas.DadosAtualizarReceita;
import br.com.leuxam.alura_challange_2.domain.receitas.DadosCriarReceita;
import br.com.leuxam.alura_challange_2.domain.receitas.DadosDetalhamentoReceita;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/receitas")
public class ReceitasController {

	@Autowired
	private ReceitasService service;

	@PostMapping
	public ResponseEntity<DadosDetalhamentoReceita> save(@RequestBody @Valid DadosCriarReceita dados,
			UriComponentsBuilder uriBuilder) {
		var receita = service.save(dados);
		var uri = uriBuilder.path("/receitas/{id}").buildAndExpand(receita.getId()).toUri();
		return ResponseEntity.created(uri).body(new DadosDetalhamentoReceita(receita));
	}

	@GetMapping
	public ResponseEntity<Page<DadosDetalhamentoReceita>> findAll(
			@PageableDefault(size = 5, sort = { "descricao" }) Pageable pageable,
			@RequestParam(value = "descricao", defaultValue = "") String descricao) {
		var receitas = service.findAll(pageable, descricao);
		return ResponseEntity.ok().body(receitas);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoReceita> findById(@PathVariable(name = "id") Long id) {
		var receita = service.findById(id);
		return ResponseEntity.ok().body(receita);
	}
	
	@GetMapping("/{ano}/{mes}")
	public ResponseEntity<Page<DadosDetalhamentoReceita>> findAllByMesAndAno(
			@PathVariable(name = "ano") Integer ano,
			@PathVariable(name = "mes") Integer mes,
			@PageableDefault(size = 5, sort = {"descricao"}) Pageable pageable){
		var receitas = service.findAllByMesAndAno(ano, mes, pageable);
		return ResponseEntity.ok().body(receitas);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DadosDetalhamentoReceita> update(@PathVariable(name = "id") Long id,
			@RequestBody @Valid DadosAtualizarReceita dados) {
		var receita = service.update(id, dados);
		return ResponseEntity.ok().body(receita);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable(name = "id") Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
