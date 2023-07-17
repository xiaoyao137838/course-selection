package com.example.courseselection.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import com.example.courseselection.models.User;
import com.example.courseselection.repository.UserRepository;

@Component
public class CustomeUserDetailsService implements UserDetailsService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private UserRepository userRepository;
	

	public CustomeUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Authenticating {}", username);
		
		if (new EmailValidator().isValid(username, null)) {
			Optional<User> userByEmailFromDB = userRepository.findOneWithAuthoritiesByEmail(username);
			return userByEmailFromDB.map(user -> createSpringSecurityUser(username, user))
					.orElseThrow(() -> new UsernameNotFoundException("The username is not found."));
		}
		
		Optional<User> userByUsernameFromDB = userRepository.findOneWithAuthoritiesByUsername(username);
		return userByUsernameFromDB.map(user -> createSpringSecurityUser(username, user))
					.orElseThrow(() -> new UsernameNotFoundException("The username is not found."));
	}


	private org.springframework.security.core.userdetails.User createSpringSecurityUser(String username, User user) {
		List<GrantedAuthority> authorities = user.getAuthorities().stream()
				.map(authority -> {
					return new SimpleGrantedAuthority(authority.getName());
				})
				.collect(Collectors.toList());
		
		return new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(),
				authorities);


	}

}
