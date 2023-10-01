package br.com.leuxam.alura_challange_2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import br.com.leuxam.alura_challange_2.domain.receitas.DadosCriarReceita;
import br.com.leuxam.alura_challange_2.domain.receitas.DadosDetalhamentoReceita;
import br.com.leuxam.alura_challange_2.domain.receitas.Receitas;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasRepository;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class ReceitasControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private ReceitasService service;
	
	@MockBean
	private ReceitasRepository receitasRepository;
	
	@Autowired
	private JacksonTester<List<DadosDetalhamentoReceita>> listDadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoReceita> dadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosCriarReceita> dadosCriar;
	
	@Captor
	private ArgumentCaptor<Pageable> captor;
	
	@BeforeAll
	void beforeAll() {
		service = new ReceitasService(receitasRepository);
	}
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 400 com mês invalido")
	void test_cenario01() throws Exception {
		
		Pageable pageable = Pageable.ofSize(5);
		
		var error = assertThrows(ValidacaoException.class, () -> {
			when(receitasRepository.findByAnoAndMes(13, 2023, pageable)).thenReturn(new PageImpl<>(new ArrayList<>()));
			when(service.findAllByMesAndAno(13, 2023, pageable)).thenReturn(new PageImpl<>(new ArrayList<>()));
		});
		
		var response = mvc.perform(get("/receitas/2023/13")).
					andReturn().getResponse();
		
		verifyNoMoreInteractions(receitasRepository);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria retornar as receitas do mes/ano e codigo HTTP 200 com mês valido")
	void test_cenario02() throws Exception {
		var receitas = mock();
		
		when(service.findAllByMesAndAno(any(), any(), any())).thenReturn(receitas);
		
		var response = mvc.perform(get("/receitas/2023/9")).
				andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(mock()
				.stream().collect(Collectors.toList())).getJson();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains(jsonEsperado);
	}
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 400 se já existe uma receita no mesmo mês com aquela descrição")
	void test_cenario03() throws Exception {
		
		when(service.save(any())).thenThrow(ValidacaoException.class);
		
		var response = mvc.perform(post("/receitas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosCriar.write(new DadosCriarReceita("desc", new BigDecimal("1000"), LocalDate.now())
							).getJson()
					))
				.andReturn().getResponse();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 201 e criar uma receita")
	void test_cenario04() throws Exception {
		var dados = new DadosCriarReceita("desc", new BigDecimal("1000"), LocalDate.now());
		var receita = new Receitas(1L,"desc", new BigDecimal("1000"), LocalDate.now());
		
		when(service.save(any())).thenReturn(receita);
		
		var response = mvc.perform(post("/receitas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosCriar.write(dados)
							.getJson()
					))
				.andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(
				new DadosDetalhamentoReceita(1L,"desc", new BigDecimal("1000"), LocalDate.now())).getJson();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
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


















