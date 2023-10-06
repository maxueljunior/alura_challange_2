package br.com.leuxam.alura_challange_2.domain.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity(name = "Users")
@Table(name = "tb_users")
@AllArgsConstructor
@NoArgsConstructor
public class Users implements UserDetails{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	private String password;
	private Boolean ativo;
	
	@ManyToMany
	@JoinTable(
			name = "users_permissions",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private List<Permissions> permissions;
	
	
	public List<String> getRoles(){
		List<String> roles = new ArrayList<>();
		for (Permissions permission : permissions) {
			roles.add(permission.getAuthority());
		}
		return roles;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return permissions;
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public String getUsername() {
		return username;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return ativo;
	}
}
