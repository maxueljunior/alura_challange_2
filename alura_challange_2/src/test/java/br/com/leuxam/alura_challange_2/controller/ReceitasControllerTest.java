package br.com.leuxam.alura_challange_2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import br.com.leuxam.alura_challange_2.domain.receitas.DadosDetalhamentoReceita;
import br.com.leuxam.alura_challange_2.domain.receitas.Receitas;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ReceitasControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ReceitasService service;
	
	@Autowired
	private JacksonTester<List<DadosDetalhamentoReceita>> dadosDetalhamento;
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 400 com mês invalido")
	void test_cenario01() throws Exception {
		
		when(service.findAllByMesAndAno(any(), any(), any())).thenThrow(ValidacaoException.class);
		
		var response = mvc.perform(get("/receitas/2023/5")).
				andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria retornar as receitas do mes/ano e codigo HTTP 200 com mês valido")
	void test_cenario02() throws Exception {
		var receitas = mock();
		
		when(service.findAllByMesAndAno(any(), any(), any())).thenReturn(receitas);
		
		var response = mvc.perform(get("/receitas/2023/9")).
				andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(mock()
				.stream().collect(Collectors.toList())).getJson();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains(jsonEsperado);
	}
	
	private Page<DadosDetalhamentoReceita> mock(){
		var receitas = new ArrayList<Receitas>();
		receitas.add(new Receitas(1L, "desc 1", new BigDecimal("1"), LocalDate.now()));
		receitas.add(new Receitas(2L, "desc 2", new BigDecimal("2"), LocalDate.now()));
		receitas.add(new Receitas(3L, "desc 3", new BigDecimal("3"), LocalDate.now()));
		receitas.add(new Receitas(4L, "desc 4", new BigDecimal("4"), LocalDate.now()));
		var dados = receitas.stream().map(DadosDetalhamentoReceita::new).collect(Collectors.toList());
		return new PageImpl<>(dados);
	}
	
}


















