package com.example.courseselection.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.courseselection.exceptions.InvalidPasswordException;
import com.example.courseselection.models.Authority;
import com.example.courseselection.models.User;
import com.example.courseselection.models.dto.UserDto;
import com.example.courseselection.repository.AuthorityRepository;
import com.example.courseselection.repository.UserRepository;
import com.example.courseselection.security.AuthoritiesConstants;
import com.example.courseselection.security.SecurityUtils;
import com.example.courseselection.utils.Constants;
import com.example.courseselection.utils.RandomUtils;
import com.example.courseselection.vm.KeyAndPasswordVM;
import com.example.courseselection.vm.PasswordVM;
import com.example.courseselection.vm.UserVM;

import jakarta.validation.Valid;

@Service
public class UserService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private UserRepository userRepository;
	
	private AuthorityRepository authorityRepository;
	
	private PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, 
			AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/* Admin */
	@Transactional(readOnly=true)
	public Page<UserDto> findAllMangedUsers(Pageable pageable) {
		return userRepository.findAllByUsernameNot(pageable, Constants.ANONYMOUS_USER)
				.map(UserDto::new);
	}
	
	@Transactional(readOnly=true)
	public List<String> findAllAuthorities() {
		return authorityRepository.findAll().stream()
				.map(Authority::getName)
				.collect(Collectors.toList());
	}

	public Optional<UserDto> findUser(String username) {
		Optional<User> user = userRepository.findOneWithAuthoritiesByUsername(username);
		return user.map(UserDto::new);
	}
	
	public User addUser(UserDto userDto) {
		String encodedPassword = passwordEncoder
				.encode(RandomUtils.generatePassword());
		
		logger.info("The authorities of addUser userDto are {}", userDto.getAuthorities());

		Set<Authority> authorities = new HashSet<>();
		userDto.getAuthorities().stream()
			.map(authorityRepository::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.forEach(authorities::add);
		
		String languageKey = userDto.getLangKey() == null ?
				Constants.DEFAULT_LANGUAGE : userDto.getLangKey();
		
		User user = User.builder()
				.username(userDto.getUsername())
				.password(encodedPassword)
				.email(userDto.getEmail())
				.phone(userDto.getPhone())
				.address(userDto.getAddress())
				.activated(true)
				.activationKey(RandomUtils.generateActivationKey())
				.imageUrl(userDto.getImageUrl())
				.firstName(userDto.getFirstName())
				.lastName(userDto.getLastName())
				.langKey(languageKey)
				.resetKey(RandomUtils.generateResetKey())
				.resetDate(Instant.now())
				.authorities(authorities)
				.build();
		
		user.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
		userRepository.save(user);
		logger.info("The user is added by admin successfully.");
		logger.info("The authorities of addUser user are {}", user.getAuthorities());
		return user;
	}

	
	public Optional<UserDto> updateUser(UserDto userDto) {
		User existingUser = null;
		try {
			existingUser = userRepository.findById(userDto.getId()).get();
		} catch (Exception e) {
			logger.error("Please use Id for upadate user.");
			existingUser = null;
		}
		
		return Optional.ofNullable(existingUser)
				.map(user -> {
					user.setUsername(userDto.getUsername());
					user.setFirstName(userDto.getFirstName());
					user.setLastName(userDto.getLastName());
					user.setEmail(userDto.getEmail());
					user.setAddress(userDto.getAddress());
					user.setPhone(userDto.getPhone());
					user.setImageUrl(user.getImageUrl());
					user.setActivated(userDto.isActivated());
					user.setLangKey(userDto.getLangKey());
					user.setLastModifiedDate(Instant.now());
					user.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().get());
					
					Set<Authority> managedAuthorities = user.getAuthorities();
					managedAuthorities.clear();
					userDto.getAuthorities().stream()
						.map(authorityRepository::findById)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.forEach(managedAuthorities::add);
					
					userRepository.save(user);
					logger.info("The user is updated by admin successfully.");
					return new UserDto(user);
				});
	}

	public void deleteUser(String username) throws Exception {
		Optional<User> optionalUser = userRepository.findUserByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new Exception("This user does not exist  for delete.");
		}
		
		userRepository.delete(optionalUser.get());
		
	}
	
	/* User */
	@Transactional(readOnly=true)
	public Optional<User> getUserWithAuthorities() {

		return SecurityUtils.getCurrentUserLogin()
				.flatMap(userRepository::findOneWithAuthoritiesByUsername);
	}

	public User registerUser(UserVM userVM) {
		String encodedPassword = passwordEncoder.encode(userVM.getPassword());
		Set<Authority> authorities = new HashSet<>();
		authorityRepository.findById(AuthoritiesConstants.USER)
			.ifPresent(authorities::add);
		
		User user = User.builder()
				.username(userVM.getUsername())
				.password(encodedPassword)
				.email(userVM.getEmail())
				.phone(userVM.getPhone())
				.address(userVM.getAddress())
				.activated(false)
				.activationKey(RandomUtils.generateActivationKey())
				.imageUrl(userVM.getImageUrl())
				.firstName(userVM.getFirstName())
				.lastName(userVM.getLastName())
				.langKey(userVM.getLangKey())
				.authorities(authorities)
				.build();
		
		user.setCreatedBy(userVM.getUsername());
		userRepository.save(user);
		logger.info("User is registered successfully.");
		logger.info("Authorities of registerUser: {}", user.getAuthorities());
		return user;
	}


	public void deleteByUser() throws Exception {
		Optional<String> usernameOptional = SecurityUtils.getCurrentUserLogin();
		if (usernameOptional.isEmpty()) {
			logger.error("Please login first.");
			throw new Exception("User not login.");
		}
		
		String username = usernameOptional.get();
		User currentUser = userRepository.findUserByUsername(username).get();
		userRepository.delete(currentUser);
		
	}

	public void updateByUser(User user, String firstName, String lastName,
			String phone, String address, String email,
			String langKey, String imageUrl) {
		
		logger.info("User service to update by user");
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhone(phone);
		user.setAddress(address);
		user.setEmail(email);
		user.setLangKey(langKey);
		user.setImageUrl(imageUrl);
		user.setLastModifiedBy(user.getUsername());
		
		userRepository.save(user);
	}

	public Optional<User> activateRegistration(String key) {
		Optional<User> user = userRepository.findUserByActivationKey(key);
		return user.map(currentUser -> {
			currentUser.setActivated(true);
			currentUser.setActivationKey(null);
			userRepository.save(currentUser);
			
			logger.info("The user is activated: {}", currentUser);
			return currentUser;
		});
	}

	public void changePassword(PasswordVM passwordVM) throws Exception {
		Optional<String> usernameOptional = SecurityUtils.getCurrentUserLogin();
		if (usernameOptional.isEmpty()) {
			logger.error("Please login first.");
			throw new Exception("User not login.");
		}
		
		logger.info("Test whether the passwords match...");
		User existingUser = usernameOptional
				.flatMap(userRepository::findUserByUsername)
				.filter(user -> {
					boolean isMatch = passwordEncoder.matches(passwordVM.oldPassword(), user.getPassword());
					logger.info("The matching result: {}", isMatch);
					return isMatch;
				})
				.orElseThrow(InvalidPasswordException::new);
		
		logger.error("The passwords match.");
		String encodedPassword = passwordEncoder.encode(passwordVM.newPassword());
		existingUser.setPassword(encodedPassword);
		userRepository.save(existingUser);
		logger.info("The password is changed successfully.");
		
	}

	public Optional<User> requestPasswordReset(String email) {
		return userRepository.findOneByEmailIgnoreCase(email)
				.filter(User::isActivated)
				.map(user -> {
					user.setResetDate(Instant.now());
					user.setResetKey(RandomUtils.generateResetKey());
					userRepository.save(user);
					logger.info("The password reset request is sent.");
					return user;
				});
	}

	public Optional<User> finishPassswordReset(KeyAndPasswordVM keyAndPasswordVM) {
		return userRepository.findUserByResetKey(keyAndPasswordVM.key())
				.filter(user -> {
					return user.getResetDate()
						.isAfter(Instant.now().minusSeconds(86400));
				})
				.map(user -> {
					String encodedPassword = passwordEncoder
							.encode(keyAndPasswordVM.newPassword());
					
					user.setPassword(encodedPassword);
					user.setResetKey(null);
					user.setResetDate(null);
					userRepository.save(user);
					
					logger.info("The password reset is successful.");
					return user;
				});
	}
	
	@Scheduled(cron="* * * * *")
	public void removeUsersUnactivated() {
		logger.info("Schedule to remove unactivated users.");
		List<User> users = userRepository
				.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(1, ChronoUnit.HOURS));
		
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()) {
			User user = iter.next();
			userRepository.delete(user);
			logger.info("Unactivated user {} is deleted Automatically.", user.getUsername());
		}
		logger.info("The unactivated users created before 1 h are removed");
	}
	
}
