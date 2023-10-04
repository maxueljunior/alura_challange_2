package br.com.leuxam.alura_challange_2.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.ArrayList;

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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import br.com.leuxam.alura_challange_2.domain.ValidacaoException;
import br.com.leuxam.alura_challange_2.domain.despesas.Categoria;
import br.com.leuxam.alura_challange_2.domain.despesas.DespesasRepository;
import br.com.leuxam.alura_challange_2.domain.receitas.ReceitasRepository;
import br.com.leuxam.alura_challange_2.domain.resumo.DetalhamentoResumo;
import br.com.leuxam.alura_challange_2.domain.resumo.Resumo;
import br.com.leuxam.alura_challange_2.domain.resumo.ResumoService;

@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ResumoControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private ResumoService service;
	
	@MockBean
	private ReceitasRepository receitasRepository;

	@MockBean
	private DespesasRepository despesasRepository;
	
	@Autowired
	private JacksonTester<DetalhamentoResumo> detalhamentoResumo;
	
	@BeforeAll
	void beforeAll() {
		service = new ResumoService(despesasRepository, receitasRepository);
	}
	
	@Test
	@DisplayName("Deveria devolver erro 400 caso mês esteja invalido")
	void teste_cenario01() throws Exception {
		
		assertThatThrownBy(
				() -> service.calcularResumo(2023, 13))
		.isInstanceOf(ValidacaoException.class)
		.hasMessage("O mês está invalido, porfavor digite um numero de 1 a 12");
		
		var response = mvc.perform(get("/resumo/2023/13"))
			.andReturn().getResponse();
		
		verifyNoInteractions(despesasRepository);
		verifyNoInteractions(receitasRepository);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria devolver erro 200 caso mês esteja valido e retornar o conteudo")
	void teste_cenario02() throws Exception {
		
		var resumo = mock();
		
		when(despesasRepository.calculateTotalDespesasByMesAndAno(2023, 1)).thenReturn(resumo.valorTotalDespesas());
		when(receitasRepository.calculateTotalReceitasByMesAndAno(2023, 1)).thenReturn(resumo.valoTotalReceitas());
		when(despesasRepository.calculateTotalAllCategorias(2023, 1)).thenReturn(resumo.totalCategorias());
		
		var response = mvc.perform(get("/resumo/2023/1"))
			.andReturn().getResponse();
		
		var jsonEsperado = detalhamentoResumo.write(mock()).getJson();
		
		verify(despesasRepository).calculateTotalDespesasByMesAndAno(2023, 1);
		verify(receitasRepository).calculateTotalReceitasByMesAndAno(2023, 1);
		verify(despesasRepository).calculateTotalAllCategorias(2023, 1);
		
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	private DetalhamentoResumo mock() {
		var despesas = new BigDecimal("2000");
		var receitas = new BigDecimal("1000");
		var saldo = despesas.subtract(receitas);
		var categorias = new ArrayList<Resumo>();
		
		categorias.add(new Resumo(Categoria.ALIMENTACAO, new BigDecimal("25")));
		categorias.add(new Resumo(Categoria.EDUCACAO, new BigDecimal("100")));
		categorias.add(new Resumo(Categoria.MORADIA, new BigDecimal("250")));
		categorias.add(new Resumo(Categoria.TRANSPORTE, new BigDecimal("100")));
		
		return new DetalhamentoResumo(receitas,
				despesas, saldo, categorias);
	}
}
















