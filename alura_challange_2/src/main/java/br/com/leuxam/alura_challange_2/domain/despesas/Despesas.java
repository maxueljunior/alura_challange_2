package br.com.leuxam.alura_challange_2.domain.despesas;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
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
}
