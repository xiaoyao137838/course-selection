package com.example.courseselection.web;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseselection.exceptions.BadRequstAlertException;
import com.example.courseselection.exceptions.EmailAlreadyUsedException;
import com.example.courseselection.exceptions.UsernameAlreadyUsedException;
import com.example.courseselection.models.Authority;
import com.example.courseselection.models.User;
import com.example.courseselection.models.dto.UserDto;
import com.example.courseselection.repository.UserRepository;
import com.example.courseselection.security.AuthoritiesConstants;
import com.example.courseselection.service.MailService;
import com.example.courseselection.service.UserService;
import com.example.courseselection.utils.Constants;
import com.example.courseselection.utils.HeaderUtils;
import com.example.courseselection.utils.PaginationUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.ADMIN)
public class AdminController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private UserService userService;
	
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	private MailService mailService;

	public AdminController(UserService userService,
			UserRepository userRepository, PasswordEncoder passwordEncoder,
			MailService mailService) {
		super();
		this.userService = userService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	
	@PostMapping("/admin/users")
	public ResponseEntity<User> addUser(@RequestBody @Valid UserDto userDto) throws Exception {
		
		logger.info("Try to add user: {}", userDto);
		
		if (userDto.getId() != null) {
			throw new BadRequstAlertException("The userDto should not have id when creating user.");
		}
		if (userRepository.findUserByUsername(
				userDto.getUsername().toLowerCase()).isPresent()) {
			throw new UsernameAlreadyUsedException();
		}
		if (userRepository.findOneByEmailIgnoreCase(userDto.getEmail()).isPresent()) {
			throw new EmailAlreadyUsedException();
		}
		
		User newUser = userService.addUser(userDto);
		mailService.sendCreationEmail(newUser);
		
		URI location = new URI("api/admin/users/".concat(newUser.getUsername()));
		HttpHeaders headers = HeaderUtils.createEntityCreationAlert(
				userDto.getUsername(), newUser.getUsername());
		return ResponseEntity.created(location).headers(headers).body(newUser);
	}

	@GetMapping("/admin/users")
	public ResponseEntity<List<UserDto>> findAllUsers(Pageable pageable) {
		final Page<UserDto> page = userService.findAllMangedUsers(pageable);
		HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(page, "/api/admin/users");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@GetMapping("/admin/users/{username:" + Constants.LOGIN_REGEX + "}")
	public ResponseEntity<UserDto> findUser(@PathVariable String username) {
		Optional<UserDto> optionalUser = userService.findUser(username);
		logger.info("The user is found? {}", optionalUser.isPresent());
		return optionalUser.isEmpty() ? 
				ResponseEntity.notFound().build() : ResponseEntity.ok(optionalUser.get());
	}
	
	@PutMapping(path="/admin/users", produces="application/json")
	public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDto userDto) 
			throws EmailAlreadyUsedException, UsernameAlreadyUsedException {
		
		Optional<User> existingUser = userRepository
				.findOneByEmailIgnoreCase(userDto.getEmail());
		
		if (existingUser.isPresent() && 
				(!existingUser.get().getId().equals(userDto.getId()))) {
			throw new EmailAlreadyUsedException();
		}
		
		existingUser = userRepository
				.findUserByUsername(userDto.getUsername());
		
		if (existingUser.isPresent() && 
				(!existingUser.get().getId().equals(userDto.getId()))) {
			throw new UsernameAlreadyUsedException();
		}
		
		Optional<UserDto> userOptional = userService.updateUser(userDto);
		HttpHeaders headers = HeaderUtils.createEntityUpdateAlert(userDto.getUsername(), userDto.getUsername());
		
		return userOptional.isEmpty() ? 
				ResponseEntity.notFound().build() : 
					ResponseEntity.accepted().headers(headers).body(userOptional.get());
	}
	
	@DeleteMapping("/admin/users/{username:" + Constants.LOGIN_REGEX + "}")
	public ResponseEntity<Void> deleteUser(@PathVariable String username) {
		try {
			userService.deleteUser(username);
			logger.info("The user is deleted by admin successfully.");
			
			HttpHeaders headers = HeaderUtils.createEntityDeletionAlert(username, username);
			return ResponseEntity.ok().headers(headers).build();
		} catch (Exception e) {
			logger.error("Delete user fails: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping("/admin/authorities")
	public ResponseEntity<List<String>> findAllAuthorities() {
		List<String> authorities = userService.findAllAuthorities();
		return ResponseEntity.ok(authorities);
	}

}
