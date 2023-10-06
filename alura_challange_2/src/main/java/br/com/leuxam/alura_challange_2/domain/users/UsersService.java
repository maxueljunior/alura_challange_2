package br.com.leuxam.alura_challange_2.domain.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersService implements UserDetailsService{
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return usersRepository.findByUsername(username);
	}
}
