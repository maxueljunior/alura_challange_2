package br.com.leuxam.alura_challange_2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.convert.Jsr310Converters.LocalDateTimeToDateConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import br.com.leuxam.alura_challange_2.domain.despesas.Categoria;
import br.com.leuxam.alura_challange_2.domain.despesas.DadosAtualizarDespesa;
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
	private JacksonTester<List<DadosDetalhamentoDespesas>> listDadosDetalhamento;
	
	@Autowired
	private JacksonTester<DadosCriarDespesa> dadosCriar;
	
	@Autowired
	private JacksonTester<DadosAtualizarDespesa> dadosAtualizar;
	
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
	
	@Test
	@DisplayName("Deveria retornar despesas que contenha a descrição passada e codigo HTTP 200")
	void test_cenario03() throws Exception {
		
		var despesas = mockListDespesas();
		when(despesasRepository.findByDescricaoLike(eq("%desp%"), any(Pageable.class))).thenReturn(despesas);
		
		var response = mvc.perform(get("/despesas")
					.param("descricao", "desp"))
				.andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(
				mockListDespesas().map(DadosDetalhamentoDespesas::new).getContent()).getJson();
		
		verify(despesasRepository).findByDescricaoLike(eq("%desp%"), any(Pageable.class));
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains(jsonEsperado);
	}
	
	
	@Test
	@DisplayName("Não deveria retornar despesas e codigo HTTP 400 caso mês não esteja correto")
	void test_cenario04() throws Exception {
		
		assertThatThrownBy(
				() ->service.findAllByMesAndAno(2023, 13, null)
		).isInstanceOf(ValidacaoException.class)
		.hasMessage("O mês está invalido, porfavor digite um numero de 1 a 12");
		
		var response = mvc.perform(get("/despesas/2023/13"))
				.andReturn().getResponse();
		
		verifyNoInteractions(despesasRepository);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria retornar despesas e codigo HTTP 200 caso mês esteja correto")
	void test_cenario05() throws Exception {
		
		var despesas = mockListDespesas();
		
		when(despesasRepository.findAllByMesAndAno(eq(2023), eq(10), any(Pageable.class))).thenReturn(despesas);
		
		var response = mvc.perform(get("/despesas/2023/10"))
				.andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(mockListDespesas()
				.map(DadosDetalhamentoDespesas::new).getContent()).getJson();
		
		verify(despesasRepository).findAllByMesAndAno(eq(2023), eq(10), any(Pageable.class));
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains(jsonEsperado);
		
	}
	
	
	@Test
	@DisplayName("Não deveria atualizar uma Despesa e codigo HTTP 400 caso já exista uma despesa")
	void test_cenario06() throws Exception {
		
		Optional<Despesas> despesa = Optional.of
				(new Despesas(1L, "despesa", new BigDecimal("1000"),
						LocalDate.now(), Categoria.ALIMENTACAO));
		
		when(despesasRepository.findById(anyLong())).thenReturn(despesa);
		when(despesasRepository.existeAlgumaReceitasByDescricao("despesa", 9, 2023))
			.thenReturn(new Despesas());
		
		assertThatThrownBy(
				() -> service.update(1L, new DadosAtualizarDespesa("despesa",
						new BigDecimal("950"), LocalDate.of(2023, 9, 15), Categoria.ALIMENTACAO))
		).isInstanceOf(ValidacaoException.class)
		.hasMessage("Já existe uma despesa despesa na data 9/2023");
		
		var response = mvc.perform(put("/despesas/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizar.write(
							new DadosAtualizarDespesa("despesa",
									new BigDecimal("950"), LocalDate.of(2023, 9, 15), Categoria.ALIMENTACAO))
							.getJson())
					).andReturn().getResponse();
		
		verify(despesasRepository, times(2)).findById(anyLong());
		verify(despesasRepository, times(2)).existeAlgumaReceitasByDescricao("despesa", 9, 2023);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria atualizar uma Despesa e codigo HTTP 200 caso não exista uma despesa")
	void test_cenario07() throws Exception {
		
		Optional<Despesas> despesa = Optional.of
				(new Despesas(1L, "despesa", new BigDecimal("1000"),
						LocalDate.now(), Categoria.ALIMENTACAO));
		
		var despesaAtt = new DadosAtualizarDespesa("despesa",
				new BigDecimal("950"), LocalDate.of(2023, 9, 15), Categoria.ALIMENTACAO);
		
		when(despesasRepository.findById(anyLong())).thenReturn(despesa);
		when(despesasRepository.existeAlgumaReceitasByDescricao("despesa", 9, 2023))
			.thenReturn(null);
		
		var response = mvc.perform(put("/despesas/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dadosAtualizar.write(despesaAtt)
							.getJson())
					).andReturn().getResponse();
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoDespesas(
				1L, "despesa", new BigDecimal("950"), LocalDate.of(2023, 9, 15), Categoria.ALIMENTACAO)
				).getJson();
		
		verify(despesasRepository, times(1)).findById(anyLong());
		verify(despesasRepository, times(1)).existeAlgumaReceitasByDescricao("despesa", 9, 2023);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
		
	}
	
	
	
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
	
	public Page<Despesas> mockListDespesas(){
		List<Despesas> despesas = new ArrayList<Despesas>();
		despesas.add(new Despesas(1L, "desp 1", new BigDecimal("100"), LocalDate.now(), Categoria.OUTROS));
		despesas.add(new Despesas(2L, "desp 2", new BigDecimal("200"), LocalDate.now(), Categoria.ALIMENTACAO));
		despesas.add(new Despesas(3L, "desp 3", new BigDecimal("300"), LocalDate.now(), Categoria.LAZER));
		despesas.add(new Despesas(4L, "desp 4", new BigDecimal("400"), LocalDate.now(), Categoria.MORADIA));
		return new PageImpl<>(despesas);
	}
	
}
