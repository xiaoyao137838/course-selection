package com.example.courseselection.web;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseselection.exceptions.BadRequstAlertException;
import com.example.courseselection.exceptions.EmailAlreadyUsedException;
import com.example.courseselection.exceptions.EmailNotFoundException;
import com.example.courseselection.exceptions.InternalServiceErrorException;
import com.example.courseselection.exceptions.InvalidPasswordException;
import com.example.courseselection.exceptions.UsernameAlreadyUsedException;
import com.example.courseselection.models.User;
import com.example.courseselection.models.dto.UserDto;
import com.example.courseselection.repository.UserRepository;
import com.example.courseselection.security.SecurityUtils;
import com.example.courseselection.service.MailService;
import com.example.courseselection.service.UserService;
import com.example.courseselection.vm.EmailVM;
import com.example.courseselection.vm.KeyAndPasswordVM;
import com.example.courseselection.vm.PasswordVM;
import com.example.courseselection.vm.UserVM;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api")
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private UserService userService;
	
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	private MailService mailService;

	public UserController(UserService userService,
			UserRepository userRepository, PasswordEncoder passwordEncoder,
			MailService mailService) {
		super();
		this.userService = userService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	
	@PostMapping("/user/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void registerUser(@RequestBody @Valid UserVM userVM) throws Exception {
		logger.info("this is user: {}", userVM.getUsername());
		
		if (!checkPasswordLength(userVM.getPassword())) {
			throw new InvalidPasswordException();
		}
		if (userVM.getId() != null) {
			throw new BadRequstAlertException("The userVM should not have id when registering user.");
		}
		if (userRepository.findUserByUsername(userVM.getUsername()).isPresent()) {
			throw new UsernameAlreadyUsedException();
		}
		if (userRepository.findOneByEmailIgnoreCase(userVM.getEmail()).isPresent()) {
			throw new EmailAlreadyUsedException();
		}
		
		User user = userService.registerUser(userVM);
		mailService.sendActivationEmail(user);
		logger.info("The user is registered successfully.");
	}
	
	@GetMapping("/user/activate")
	public void activateUser(@RequestParam String key) throws Exception {
		Optional<User> user = userService.activateRegistration(key);
		if (user.isEmpty()) {
			throw new InternalServiceErrorException("No user found for this activation key.");
		}
	}
	
	@GetMapping("/user/check-auth")
	public String checkAuthentication(HttpServletRequest request) {
		logger.info("Check whether the current user is authenticated");
		return request.getRemoteUser();
	}
	
	@GetMapping("/user/account")
	public UserDto checkAccount() throws Exception {
		return userService.getUserWithAuthorities()
				.map(UserDto::new)
				.orElseThrow(() -> new InternalServiceErrorException("User could not be found."));
			
	}
	
	@PostMapping("/user/change-password")
	public void changePassword(@RequestBody PasswordVM passwordVM) throws Exception {
		if (!checkPasswordLength(passwordVM.newPassword())) {
			logger.error("The password format is not correct.");
			throw new InvalidPasswordException();
		}
		userService.changePassword(passwordVM);
	}
	
	@PostMapping("/user/reset-password/init")
	public void initPasswordReset(@RequestBody EmailVM emailVM) throws Exception {
		String email = emailVM.email();
		logger.info("The email to send is {}", email);
		User user = userService.requestPasswordReset(email)
				.orElseThrow(EmailNotFoundException::new);
		
		mailService.sendPasswordReset(user);
	}
	
	@PostMapping("/user/reset-password/finish")
	public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPasswordVM) 
			throws InvalidPasswordException, InternalServiceErrorException {
		
		if (!checkPasswordLength(keyAndPasswordVM.newPassword())) {
			throw new InvalidPasswordException();
		}
		
		Optional<User> user = userService.finishPassswordReset(keyAndPasswordVM);
		
		if (user.isEmpty()) {
			throw new InternalServiceErrorException("No user found for the reset key.");
		}
	}
	
	private boolean checkPasswordLength(String password) {
		return StringUtils.isNotBlank(password) &&
				password.length() >= UserVM.PASSWORD_MIN_LENGTH &&
				password.length() <= UserVM.PASSWORD_MAX_LENGTH;
	}

	@PutMapping("/user")
	public void updateUser(@RequestBody @Valid UserDto userDto) throws Exception {
		String username = SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new InternalServiceErrorException("Current user is not logged in."));
		User user = userRepository.findUserByUsername(username).get();
		
		Optional<User> existingUser = userRepository
				.findOneByEmailIgnoreCase(userDto.getEmail());
		if (existingUser.isPresent() && 
				!(existingUser.get().getUsername().equals(username))) {
			throw new EmailAlreadyUsedException(); 
		}
		logger.info("Email checked for update by user.");
		
		existingUser = userRepository.findUserByUsername(userDto.getUsername());
		if (existingUser.isPresent() &&
				!(existingUser.get().getUsername().equals(username))) {
			throw new UsernameAlreadyUsedException();
		}
		logger.info("Username checked for update by user.");
		
		try {
			userService.updateByUser(user,
									userDto.getFirstName(),
									userDto.getLastName(),
									userDto.getPhone(),
									userDto.getAddress(),
									userDto.getEmail(),
									userDto.getLangKey(),
									userDto.getImageUrl());
			
			logger.info("The user is updated successfully.");
		} catch (Exception e) {
			logger.error("Update by user fails: {}", e.getMessage());
		}
	}
	
	@DeleteMapping("/user") 
	public void deleteUser() throws Exception {
		String username = SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new InternalServiceErrorException("Current user is not logged in."));
		
		try {
			userService.deleteUser(username);
			logger.info("The user is deleted successfully.");
		} catch (Exception e) {
			logger.error("Delete by user fails: {}", e.getMessage());
			e.printStackTrace();
		}
	}
	

}
