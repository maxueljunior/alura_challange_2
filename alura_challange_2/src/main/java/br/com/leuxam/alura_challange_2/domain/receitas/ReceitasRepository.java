package br.com.leuxam.alura_challange_2.domain.receitas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceitasRepository extends JpaRepository<Receitas, Long>{

	@Query("SELECT r FROM Receitas r WHERE r.descricao = :descricao AND MONTH(r.data) = :mes")
	Receitas existeAlgumaReceitasByDescricao(
			@Param("descricao") String descricao,
			@Param("mes") int mes);
}
