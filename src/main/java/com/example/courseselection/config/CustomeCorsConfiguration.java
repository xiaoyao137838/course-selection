package com.example.courseselection.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;


@Component
//@ConfigurationProperties(prefix="jhipster.cors", ignoreUnknownFields=false)
public class CustomeCorsConfiguration extends CorsConfiguration {
	
	@Value("${jhipster.cors.allowed-origins}")
	private List<String> allowedOrigins;
	
	@Value("${jhipster.cors.allowed-methods}")
	private List<String> allowedMethods;
	
	@Value("${jhipster.cors.allowed-headers}")
	private List<String> allowedHeaders;
	
	@Value("${jhipster.cors.exposed-headers}")
	private List<String> exposedHeaders;
	
	@Value("${jhipster.cors.allow-credentials}")
	private Boolean allowCredentials;
	
	@Value("${jhipster.cors.max-age}")
	private Long maxAge;

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public List<String> getAllowedMethods() {
		return allowedMethods;
	}

	public List<String> getAllowedHeaders() {
		return allowedHeaders;
	}

	public List<String> getExposedHeaders() {
		return exposedHeaders;
	}

	public Boolean getAllowCredentials() {
		return allowCredentials;
	}

	public Long getMaxAge() {
		return maxAge;
	}
	
}
