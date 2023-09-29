package br.com.leuxam.alura_challange_2.domain.receitas;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceitasRepository extends JpaRepository<Receitas, Long>{

	@Query(""" 
			SELECT r 
			FROM Receitas r 
			WHERE r.descricao = :descricao 
			AND MONTH(r.data) = :mes
			AND YEAR(r.data) = :ano
			""")
	Receitas existeAlgumaReceitasByDescricao(
			@Param("descricao") String descricao,
			@Param("mes") int mes,
			@Param("ano") int ano);
	
	Page<Receitas> findByDescricaoLike(String descricao, Pageable pageable);
	
	@Query("""
			SELECT r
			FROM Receitas r
			WHERE YEAR(r.data) = :ano
			AND MONTH(r.data) = :mes
			""")
	Page<Receitas> findByAnoAndMes(
			@Param("ano") Integer ano,
			@Param("mes") Integer mes,
			Pageable pagaeble);
	
	@Query("""
			SELECT SUM(r.valor)
			FROM Receitas r
			WHERE YEAR(r.data) = :ano
			AND MONTH(r.data) = :mes
			""")
	BigDecimal calculateTotalReceitasByMesAndAno(Integer ano, Integer mes);
}
