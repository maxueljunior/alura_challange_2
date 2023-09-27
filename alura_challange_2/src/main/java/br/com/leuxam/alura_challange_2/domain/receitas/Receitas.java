package br.com.leuxam.alura_challange_2.domain.receitas;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Receitas")
@Table(name = "tb_receitas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Receitas {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String descricao;
	private BigDecimal valor;
	
	private LocalDate data;
	
	public Receitas(DadosCriarReceita dados) {
		this.descricao = dados.descricao();
		this.valor = dados.valor();
		this.data = dados.data();
	}
}





