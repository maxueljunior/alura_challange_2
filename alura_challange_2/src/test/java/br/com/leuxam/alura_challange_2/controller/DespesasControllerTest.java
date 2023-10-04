package br.com.leuxam.alura_challange_2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import br.com.leuxam.alura_challange_2.domain.despesas.Categoria;
import br.com.leuxam.alura_challange_2.domain.despesas.DadosCriarDespesa;
import br.com.leuxam.alura_challange_2.domain.despesas.DadosDetalhamentoDespesas;
import br.com.leuxam.alura_challange_2.domain.despesas.Despesas;
import br.com.leuxam.alura_challange_2.domain.despesas.DespesasRepository;
import br.com.leuxam.alura_challange_2.domain.despesas.DespesasService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestInstance(Lifecycle.PER_CLASS)
class DespesasControllerTest {
	
	private DespesasService service;
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private DespesasRepository despesasRepository;
	
	@Autowired
	private JacksonTester<DadosDetalhamentoDespesas> dadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosCriarDespesa> dadosCriar;
	
	@Captor
	private ArgumentCaptor<Despesas> captor;
	
	@BeforeAll
	void beforeAll() {
		service = new DespesasService(despesasRepository);
	}
	
	@Test
	@DisplayName("Não deveria criar uma Despesa caso e retorna codigo HTTP 400 caso já exista uma despesa naquele mês")
	void test_cenario01() throws Exception {
		
		var despesa = mockDadosCriarDespesa();
		var data = LocalDate.now();
		var mes = data.getMonthValue();
		var ano = data.getYear();
		
		when(despesasRepository.existeAlgumaReceitasByDescricao("despesa", mes, ano)).thenReturn(new Despesas());
		
		assertThatThrownBy(
				() -> service.save(despesa)).isInstanceOf(ValidacaoException.class)
				.hasMessage("Já existe uma despesa despesa na data " + mes + "/" + ano);
		
		var response = mvc.perform(post("/despesas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(dadosCriar.write(despesa).getJson()
				)).andReturn().getResponse();
		
		verify(despesasRepository, times(2)).existeAlgumaReceitasByDescricao("despesa", mes, ano);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
		
	}
	
	@Test
	@DisplayName("Deveria criar uma Despesa caso e retorna codigo HTTP 201 caso não exista uma despesa naquele mês")
	void test_cenario02() throws Exception {
		
		var despesa = mockDadosCriarDespesa();
		var data = LocalDate.now();
		var mes = data.getMonthValue();
		var ano = data.getYear();
		
		when(despesasRepository.existeAlgumaReceitasByDescricao("despesa", mes, ano)).thenReturn(null);
		when(despesasRepository.save(any(Despesas.class))).thenAnswer(arguments -> {
			Despesas desp = arguments.getArgument(0);
			desp.setId(1L);
			return desp;
		});
		
		var response = mvc.perform(post("/despesas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosCriar.write(despesa).getJson())
					)
				.andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoDespesas(mockDespesa())).getJson();
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	/*
	@Test
	void testFindAll() {
		fail("Not yet implemented");
	}

	@Test
	void testFindAllByMesAndAno() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdate() {
		fail("Not yet implemented");
	}*/
	
	private DadosCriarDespesa mockDadosCriarDespesa() {
		var despesas = new DadosCriarDespesa("despesa",
				new BigDecimal("1000"), LocalDate.now(), Categoria.OUTROS);
		return despesas;
	}
	
	private Despesas mockDespesa() {
		var despesas = new Despesas(1L ,"despesa",
				new BigDecimal("1000"), LocalDate.now(), Categoria.OUTROS);
		return despesas;
	}
}
