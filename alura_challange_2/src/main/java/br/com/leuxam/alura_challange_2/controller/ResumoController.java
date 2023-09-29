package br.com.leuxam.alura_challange_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.leuxam.alura_challange_2.domain.resumo.DetalhamentoResumo;
import br.com.leuxam.alura_challange_2.domain.resumo.ResumoService;

@RestController
@RequestMapping("/resumo")
public class ResumoController {
	
	@Autowired
	private ResumoService service;
	
	@GetMapping("/{ano}/{mes}")
	public ResponseEntity<DetalhamentoResumo> resumo(
			@PathVariable(name = "ano") Integer ano,
			@PathVariable(name = "mes") Integer mes){
		
		var dados = service.calcularResumo(ano, mes);
		return ResponseEntity.ok().body(dados);
	}
}
