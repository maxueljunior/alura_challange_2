package br.com.leuxam.alura_challange_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

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
	public ResponseEntity<DadosDetalhamentoReceita> save(
			@RequestBody @Valid DadosCriarReceita dados, UriComponentsBuilder uriBuilder){
		var receita = service.save(dados);
		var uri = uriBuilder.path("/receitas/{id}").buildAndExpand(receita.getId()).toUri();
		return ResponseEntity.created(uri).body(new DadosDetalhamentoReceita(receita));
	}
}






