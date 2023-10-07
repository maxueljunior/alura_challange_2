package br.com.leuxam.alura_challange_2.domain.users;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Permissions")
@Table(name = "tb_permissions")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Permissions implements GrantedAuthority{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String descricao;
	
	@ManyToMany(mappedBy = "permissions")
	private List<Users> usuarios;

	@Override
	public String getAuthority() {
		return descricao;
	}

	@Override
	public String toString() {
		return "Permissions [id=" + id + ", descricao=" + descricao + ", usuarios=" + usuarios + "]";
	}
	
	
}
