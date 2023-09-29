package br.com.leuxam.alura_challange_2.domain.despesas;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Despesas")
@Table(name = "tb_despesas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Despesas {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String descricao;
	private BigDecimal valor;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate data;
	
	@Enumerated(EnumType.STRING)
	private Categoria categoria;

	public Despesas(DadosCriarDespesa dados) {
		this.descricao = dados.descricao();
		this.valor = dados.valor();
		this.data = dados.data();
		this.categoria = dados.categoria();
		if(dados.categoria() == null) {
			this.categoria = Categoria.OUTROS;
		}
	}
	
	public Despesas(Categoria categoria, BigDecimal valor) {
		this.categoria = categoria;
		this.valor = valor;
	}

	public void atualizarDados(DadosAtualizarDespesa dados) {
		if(dados.descricao() != null) {
			this.descricao = dados.descricao();
		}
		
		if(dados.valor() != null) {
			this.valor = dados.valor();
		}
		
		if(dados.data() != null) {
			this.data = dados.data();
		}
		
		if(dados.categoria() != null) {
			this.categoria = dados.categoria();
		}
	}
}
