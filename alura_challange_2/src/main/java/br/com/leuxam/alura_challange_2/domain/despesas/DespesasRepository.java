package br.com.leuxam.alura_challange_2.domain.despesas;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.leuxam.alura_challange_2.domain.resumo.Resumo;
import jakarta.persistence.ConstructorResult;

@Repository
public interface DespesasRepository extends JpaRepository<Despesas, Long>{
	
	@Query("""
			SELECT d 
			FROM Despesas d 
			WHERE d.descricao = :descricao 
			AND MONTH(d.data) = :mes
			AND YEAR(d.data) = :ano
			""")
	Despesas existeAlgumaReceitasByDescricao(
			@Param(value = "descricao") String descricao,
			@Param(value = "mes") int mes,
			@Param(value = "ano") int ano);
	
//	@Query("""
//			SELECT d
//			FROM Despesas d
//			WHERE d.descricao LIKE %:descricao%
//			""")
	Page<Despesas> findByDescricaoLike(
			@Param(value = "descricao") String descricao,
			Pageable pageable);
	
	@Query("""
			SELECT d
			FROM Despesas d
			WHERE YEAR(d.data) = :ano
			AND MONTH(d.data) = :mes
			""")
	Page<Despesas> findAllByMesAndAno(Integer ano, Integer mes, Pageable pageable);
	
	@Query("""
			SELECT SUM(d.valor)
			FROM Despesas d
			WHERE YEAR(d.data) = :ano
			AND MONTH(d.data) = :mes
			""")
	BigDecimal calculateTotalDespesasByMesAndAno(
			@Param("ano") Integer ano,
			@Param("mes") Integer mes);
	
	@Query("""
			SELECT new br.com.leuxam.alura_challange_2.domain.resumo.Resumo(d.categoria, SUM(d.valor) as total) 
			FROM Despesas d
			WHERE YEAR(d.data) = :ano
			AND MONTH(d.data) = :mes
			GROUP BY d.categoria
			""")
	List<Resumo> calculateTotalAllCategorias(
			@Param("ano") Integer ano,
			@Param("mes") Integer mes);
	

}
