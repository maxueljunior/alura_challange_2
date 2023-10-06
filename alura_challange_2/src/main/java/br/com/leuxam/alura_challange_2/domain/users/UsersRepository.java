package br.com.leuxam.alura_challange_2.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
	
	UserDetails findByUsername(String username);
}
