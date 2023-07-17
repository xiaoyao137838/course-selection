package com.example.courseselection.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import net.logstash.logback.util.StringUtils;

public class JwtFilter implements Filter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final TokenProvider tokenProvider;

	public JwtFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest servletRequest = (HttpServletRequest)request;
		String token = resolveToken(servletRequest);
		
		logger.info("Jwt token from headers: {}", token);
		
		if (!StringUtils.isBlank(token) && tokenProvider.validateToken(token)) {
			Authentication authentication = tokenProvider
					.createAuthenticationFromToken(token);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			logger.info("Jwt token is validated and authentication is stored in context.");
		}
		chain.doFilter(servletRequest, response);
		
	}

	private String resolveToken(HttpServletRequest servletRequest) {
		String bearerToken = servletRequest.getHeader(JwtConfigurer.AUTHORIZATION_HEADER);
		logger.info("Bearer token from headers: {}", bearerToken);
		
		if (!StringUtils.isBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.substring(7, bearerToken.length());
			logger.info("After substring, Jwt token from headers: {}", token);
			return token;
		}
		
		return null;
	}

}
