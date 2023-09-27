package br.com.leuxam.alura_challange_2.domain.despesas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DespesasRepository extends JpaRepository<Despesas, Long>{
	
}
