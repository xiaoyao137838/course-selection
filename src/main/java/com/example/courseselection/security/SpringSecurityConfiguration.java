package com.example.courseselection.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.thymeleaf.util.ListUtils;

import com.example.courseselection.security.jwt.JwtConfigurer;
import com.example.courseselection.security.jwt.TokenProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SpringSecurityConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final CustomeUserDetailsService userDetailsService;
	private final TokenProvider tokenProvider;
	private final CorsConfiguration corsConfiguration;

	public SpringSecurityConfiguration(CustomeUserDetailsService userDetailsService, 
			TokenProvider tokenProvider, CorsConfiguration corsConfiguration) {
		super();
		this.userDetailsService = userDetailsService;
		this.tokenProvider = tokenProvider;
		this.corsConfiguration = corsConfiguration;
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		var authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authenticationProvider);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, CorsFilter corsFilter)
			throws Exception {
		
		http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
		http.authorizeHttpRequests(
				auth -> auth
				.requestMatchers("/authenticate").permitAll()
				.requestMatchers("/api/user/register").permitAll()
				.requestMatchers("/api/user/activate").permitAll()
				.requestMatchers("/api/user/reset-password/init").permitAll()
				.requestMatchers("/api/user/reset-password/finish").permitAll()
				.requestMatchers("/management/health").permitAll()
				.requestMatchers("/management/info").permitAll()
				.requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
				.anyRequest().authenticated());	
		
		http.httpBasic();
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});
		http.apply(jwtConfigurer());
		
		return http.build();
	}
	
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		if (!ListUtils.isEmpty(corsConfiguration.getAllowedOrigins())) {
			logger.debug("Registering CORS filter");
			source.registerCorsConfiguration("/api/**", corsConfiguration);
			source.registerCorsConfiguration("/management/**", corsConfiguration);
			source.registerCorsConfiguration("/v2/api-docs", corsConfiguration);
		}
		return new CorsFilter(source);
	}

	private JwtConfigurer jwtConfigurer() {
		return new JwtConfigurer(tokenProvider);
	}

}
