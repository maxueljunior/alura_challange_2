package br.com.leuxam.alura_challange_2.domain.receitas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "Receitas")
@Table(name = "tb_receitas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    public void atualizarDados(DadosAtualizarReceita dados) {
		if(dados.descricao() != null){
			this.descricao = dados.descricao();
		}

		if(dados.valor() != null){
			this.valor = dados.valor();
		}

		if(dados.data() != null){
			this.data = dados.data();
		}
    }
}





