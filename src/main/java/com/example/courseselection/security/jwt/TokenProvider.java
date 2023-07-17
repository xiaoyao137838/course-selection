package com.example.courseselection.security.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class TokenProvider {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String AUTHORITIES_KEY = "auth";
	
	private static final long EXPIRATION = 1 * 24 * 60 * 60 * 1000;
	
	private static final long EXPIRATION_REMEMBBER_ME = 7 * 24 * 60 * 60 * 1000;
	
	private static final String SECRETE_KEY = "thisisnotagoodsecretkey";

	public String createTokenFromAuthentication(Authentication authentication,
			boolean rememberMe) {
		
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
		
		logger.info("The authorities of the user are {}", authorities);
		
		long nowInMilliseconds = new Date().getTime();
		
		Date expiration = rememberMe ? 
				new Date(nowInMilliseconds + EXPIRATION_REMEMBBER_ME) : 
					new Date(nowInMilliseconds + EXPIRATION);
		
		String token = Jwts.builder()
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, authorities)
				.signWith(SignatureAlgorithm.HS512, SECRETE_KEY)
				.setExpiration(expiration)
				.compact();
		
		return token;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
			.setSigningKey(SECRETE_KEY)	
			.parseClaimsJws(token);
			return true;
			
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature.");
			logger.trace("Invalid JWT signature trace: {}", e);
			
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token.");
			logger.trace("Invalid JWT token track: {}", e);
			
		} catch (ExpiredJwtException e) {
			logger.error("Expired JWT token.");
			logger.trace("Expired JWT token track: {}", e);
			
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token: {}", e);
			logger.trace("Unsupported JWT token track: {}", e);
			
		} catch (IllegalArgumentException e) {
			logger.error("Invalid JWT token compact of handler.");
			logger.trace("Invalid JWT token campact of handler track: {}", e);
		}
		
		return false;
	}

	public Authentication createAuthenticationFromToken(String token) {
		
		Claims claims = Jwts.parser()
				.setSigningKey(SECRETE_KEY)
				.parseClaimsJws(token).getBody();
		
		String[] authoritiesArray = claims.get(AUTHORITIES_KEY)
				.toString().split(",");
		logger.info("authoritiesArray is {}", authoritiesArray);
		
		Collection<? extends GrantedAuthority> authorities = 
				Arrays.stream(authoritiesArray)
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());

		User principle = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principle, token, authorities);
	}

}
