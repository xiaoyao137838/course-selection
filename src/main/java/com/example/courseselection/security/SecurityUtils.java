package com.example.courseselection.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {
	
	private SecurityUtils() {
		
	}
	
	public static Optional<String> getCurrentUserLogin() {
		Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		
		return Optional.ofNullable(authentication)
				.map(auth -> {
					if (auth.getPrincipal() instanceof
							UserDetails principle) {
						return principle.getUsername();
					} else if (authentication.getPrincipal() instanceof
							String username) {
						return username;
					}
					return null;
				});
	}
	
	public static Optional<String> getCurrentUserJwt() {
		Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		
		return Optional.ofNullable(authentication)
				.map(auth -> {
					if (auth.getCredentials() instanceof String jwtToken) {
						return jwtToken;
					}
					return null;
				});
	}
	
	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		
		return Optional.ofNullable(authentication)
				.map(auth -> auth.getAuthorities().stream()
					.noneMatch(authority -> authority.getAuthority()
							.equals(AuthoritiesConstants.ANONYMOUS)))
				.orElse(false);
	}
	
	public static boolean isCurrentUserInRole(String authority) {
		Authentication authentication = SecurityContextHolder
				.getContext().getAuthentication();
		
		return Optional.ofNullable(authentication)
				.map(auth -> auth.getAuthorities().stream()
						.anyMatch(grantedAuthority -> 
						grantedAuthority.getAuthority().equals(authority)))
				.orElse(false);
	}
}
