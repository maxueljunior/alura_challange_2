package br.com.leuxam.alura_challange_2.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.leuxam.alura_challange_2.domain.users.Permissions;
import br.com.leuxam.alura_challange_2.domain.users.Users;

@Service
public class TokenService {
	
	@Value("${api.token.security.key}")
	private String secret;
	
	public String createToken(Users users) {
		try {
			
			Algorithm algorithm = Algorithm.HMAC512(secret);
			return JWT.create()
					.withIssuer("API with Alura challange 2")
					.withSubject(users.getUsername())
					.withClaim("roles", users.getRoles())
					.withExpiresAt(dataExpiracao())
					.sign(algorithm);
			
		}catch(JWTCreationException exception) {
			throw new JWTCreationException("Erro ao gerar token!!", exception);
		}
	}
	
	public String getSubject(String token) {
		DecodedJWT decodedJWT;
		try {
			
			Algorithm algorithm = Algorithm.HMAC512(secret);
			JWTVerifier verifier = JWT.require(algorithm)
						.withIssuer("API with Alura challange 2")
						.build();
			
			return verifier.verify(token).getSubject();
			
		}catch(JWTVerificationException exception) {
			throw new JWTVerificationException("Token invalido ou expirado!!");
		}
	}
	
	public List<String> getRoles(String token) {
		try {
			
			Algorithm algorithm = Algorithm.HMAC512(secret);
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer("API with Alura challange 2")
					.build();
			
			return verifier.verify(token).getClaim("roles").asList(String.class);
			
		}catch(JWTVerificationException exception) {
			throw new JWTVerificationException("Token invalido ou expirado!!");
		}
	}

	private Instant dataExpiracao() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
}
