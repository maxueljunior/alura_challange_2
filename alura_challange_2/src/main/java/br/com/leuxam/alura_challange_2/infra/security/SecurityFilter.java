package br.com.leuxam.alura_challange_2.infra.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.leuxam.alura_challange_2.domain.users.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter{
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var token = recuperarToken(request);
		
		if(token != null) {
			var subject = tokenService.getSubject(token);
			var user = usersRepository.findByUsername(subject);
			var authentication = new UsernamePasswordAuthenticationToken
					(user.getUsername(), null, user.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authentication);;
		}
		
		filterChain.doFilter(request, response);
	}

	private String recuperarToken(HttpServletRequest request) {
		var token = request.getHeader("Authorization");
		
		if(token != null) {
			return token.replace("Bearer ", "");
		}
		return null;
	}
	
}
