package com.example.courseselection.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.courseselection.security.jwt.JwtConfigurer;
import com.example.courseselection.security.jwt.TokenProvider;
import com.example.courseselection.vm.LoginVM;

@Controller
public class JwtController {
	
	public static class JwtToken {
		
		private String token;

		public JwtToken(String token) {
			super();
			this.token = token;
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final AuthenticationManager authenticationManager;
	
	private final TokenProvider tokenProvider;
	
	
	public JwtController(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
		super();
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/authenticate")
	@ResponseBody
	public ResponseEntity<?> authenticate(@RequestBody LoginVM loginVM) {
		
		logger.info("The login information: {}", loginVM);
		
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(loginVM.username(), loginVM.password());

		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		logger.info("From /authenticate, principal is {}", authentication.getPrincipal());
		logger.info("From /authenticate, authorities are {}", authentication.getAuthorities());
		logger.info("From /authenticate, credential is {}", authentication.getCredentials());
		
		String token = tokenProvider
				.createTokenFromAuthentication(authentication, loginVM.rememberMe());
		logger.info("JWT token is {}", token);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtConfigurer.AUTHORIZATION_HEADER, "Bearer " + token);
		logger.info("The headers are {}", httpHeaders);
		
		return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
		
	}

}
