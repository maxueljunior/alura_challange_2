package br.com.leuxam.alura_challange_2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import br.com.leuxam.alura_challange_2.domain.receitas.DadosAtualizarReceita;
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
	private JacksonTester<DadosAtualizarReceita> dadosAtualizar;
	
	@Autowired
	private JacksonTester<DadosCriarReceita> dadosCriar;
	
	private Pageable pageable = Pageable.ofSize(5);
	
	@BeforeAll
	void beforeAll() {
		service = new ReceitasService(receitasRepository);
	}
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 400 com mês invalido")
	void test_cenario01() throws Exception {
		
		var error = assertThrows(ValidacaoException.class, () -> {
			when(receitasRepository.findByAnoAndMes(eq(13), eq(2023), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));
			service.findAllByMesAndAno(13, 2023, pageable);
		});
		
		var response = mvc.perform(get("/receitas/2023/13")).
					andReturn().getResponse();
		
		verifyNoInteractions(receitasRepository);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria retornar as receitas do mes/ano e codigo HTTP 200 com mês valido")
	void test_cenario02() throws Exception {
		var receitas = mock();
		
		when(receitasRepository.findByAnoAndMes(eq(2023), eq(9), any(Pageable.class))).thenReturn(receitas);
		
		var response = mvc.perform(get("/receitas/2023/9"))
				.andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(mock().stream()
				.map(DadosDetalhamentoReceita::new)
				.collect(Collectors.toList())
				).getJson();
		
		verify(receitasRepository).findByAnoAndMes(eq(2023), eq(9), any(Pageable.class));
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains(jsonEsperado);
	}
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 400 se já existe uma receita no mesmo mês com aquela descrição")
	void test_cenario03() throws Exception {
		
		var data = LocalDate.now();
		var receita = new Receitas(1L,"desc", new BigDecimal("1000"), data);
		var mes = data.getMonthValue();
		var ano = data.getYear();
		
		when(receitasRepository.existeAlgumaReceitasByDescricao("desc", mes, ano)).thenReturn(receita);
		
		var response = mvc.perform(post("/receitas")
					.contentType(MediaType.APPLICATION_JSON)
					.content(
							dadosCriar.write(new DadosCriarReceita("desc", new BigDecimal("1000"), data)
							).getJson()
					))
				.andReturn().getResponse();
		
		verify(receitasRepository).existeAlgumaReceitasByDescricao("desc", mes, ano);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 201 e criar uma receita")
	void test_cenario04() throws Exception {
		
		var data = LocalDate.now();
		var mes = data.getMonthValue();
		var ano = data.getYear();
		
		var dados = new DadosCriarReceita("desc", new BigDecimal("1000"), data);
		var receita = new Receitas(1L,"desc", new BigDecimal("1000"), data);
		
		when(receitasRepository.existeAlgumaReceitasByDescricao("desc", mes, ano)).thenReturn(null);
		
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
	
	@Test
	@DisplayName("Deveria retornar codigo HTTP 200 com todas as receitas com a descrição passada")
	void test_cenario05() throws Exception {
		var receitas = mock();
		
		when(receitasRepository.findByDescricaoLike(anyString(), any(Pageable.class))).thenReturn(receitas);
		
		var response = mvc.perform(get("/receitas")
					.param("descricao", "desc"))
				.andReturn().getResponse();
		
		var jsonEsperado = listDadosDetalhamento.write(mock()
					.map(DadosDetalhamentoReceita::new)
					.getContent()
				).getJson();
		
		verify(receitasRepository).findByDescricaoLike(anyString(), any(Pageable.class));
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).contains(jsonEsperado);
	}
	
	@Test
	@DisplayName("Não deveria atualizar receita e retornar codigo HTTP 400 se caso alguma receita já existir")
	void test_cenario06() throws Exception {
		
		var data = LocalDate.of(2023, 8, 15);
		
		Optional<Receitas> receita = Optional.of(new Receitas(1L,"desc", new BigDecimal("1000"), data));
		
		when(receitasRepository.findById(anyLong())).thenReturn(receita);
		
		when(receitasRepository.existeAlgumaReceitasByDescricao("desc", 10, 2023)).thenReturn(new Receitas(1L, "desc", new BigDecimal("250"), LocalDate.now()));
		
		var response = mvc.perform(put("/receitas/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						dadosAtualizar
						.write(
								new DadosAtualizarReceita("desc", new BigDecimal("250"), LocalDate.now())
						).getJson())
				).andReturn().getResponse();
		
		verify(receitasRepository).findById(anyLong());
		verify(receitasRepository).existeAlgumaReceitasByDescricao("desc", 10, 2023);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria atualizar receita e retornar codigo HTTP 200 se caso dados informados corretos")
	void test_cenario07() throws Exception {
		
		var data = LocalDate.of(2023, 8, 15);
		
		Optional<Receitas> receita = Optional.of(new Receitas(1L,"desc", new BigDecimal("1000"), data));
		
		when(receitasRepository.findById(anyLong())).thenReturn(receita);
		
		when(receitasRepository.existeAlgumaReceitasByDescricao("desc", 10, 2023)).thenReturn(null);
		
		var response = mvc.perform(put("/receitas/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						dadosAtualizar
						.write(
								new DadosAtualizarReceita("desc", new BigDecimal("250"), LocalDate.now())
						).getJson())
				).andReturn().getResponse();
		
		verify(receitasRepository).findById(anyLong());
		verify(receitasRepository).existeAlgumaReceitasByDescricao("desc", 10, 2023);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		
		var jsonEsperado = dadosDetalhamento.write(new DadosDetalhamentoReceita(1L, "desc", new BigDecimal("250"), LocalDate.now())).getJson();
		
		assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	private Page<Receitas> mock(){
		var receitas = new ArrayList<Receitas>();
		receitas.add(new Receitas(1L, "desc 1", new BigDecimal("1"), LocalDate.now()));
		receitas.add(new Receitas(2L, "desc 2", new BigDecimal("2"), LocalDate.now()));
		receitas.add(new Receitas(3L, "desc 3", new BigDecimal("3"), LocalDate.now()));
		receitas.add(new Receitas(4L, "desc 4", new BigDecimal("4"), LocalDate.now()));
		return new PageImpl<>(receitas);
	}
	
}


















