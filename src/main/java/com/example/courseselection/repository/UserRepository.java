package com.example.courseselection.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseselection.models.User;

public interface UserRepository extends JpaRepository<User, Long>  {
	
	@EntityGraph(attributePaths="authorities")
	Optional<User> findOneWithAuthoritiesByEmail(String email);
	
	@EntityGraph(attributePaths="authorities")
	Optional<User> findOneWithAuthoritiesByUsername(String username);

	Optional<User> findUserByUsername(String username);

	Page<User> findAllByUsernameNot(Pageable pageable, String anonymousUser);

	Optional<User> findOneByEmailIgnoreCase(String email);

	Optional<User> findUserByActivationKey(String key);

	Optional<User> findUserByResetKey(String key);

	List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

}
