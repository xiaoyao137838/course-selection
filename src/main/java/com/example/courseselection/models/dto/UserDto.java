package com.example.courseselection.models.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.hibernate.annotations.BatchSize;

import com.example.courseselection.models.AbstractAuditingEntity;
import com.example.courseselection.models.Authority;
import com.example.courseselection.models.User;
import com.example.courseselection.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {	
	
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 50)
	@Pattern(regexp=Constants.LOGIN_REGEX)
	private String username;
	
	@Email
	@Size(min = 5, max = 250)
	private String email;
	
	@Size(max=50)
	private String firstName;
	
	@Size(max=50)
	private String lastName;
	
	@Size(max = 50)
	private String address;
	
	@Size(max = 20)
	private String phone;
	
	@NotNull
	@Builder.Default
	private boolean activated = false;
	
	@Size
	private String langKey;
	
	@Size(max=256)
	private String imageUrl;
	
	@JsonIgnore
	@Size(max=20)
	private String activationKey;
	
	@JsonIgnore
	@Size(max=20)
	private String resetKey;
	
	@Builder.Default
	private Instant resetDate = null;

	private Set<String> authorities = new HashSet<>();

	private String createdBy;

	private Instant createdDate;

	private String lastModifiedBy;

	private Instant lastModifiedDate;
	
	public UserDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.address = user.getAddress();
		this.phone = user.getPhone();
		this.activated = user.isActivated();
		this.imageUrl = user.getImageUrl();
		this.langKey = user.getLangKey();
		this.createdBy = user.getCreatedBy();
		this.createdDate = user.getCreatedDate();
		this.lastModifiedBy = user.getLastModifiedBy();
		this.lastModifiedDate = user.getLastModifiedDate();
		
		this.authorities = user.getAuthorities().stream()
				.map(Authority::getName)
				.collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", email=" + email + ", firstName=" + firstName + ", lastName="
				+ lastName + ", address=" + address + ", phone=" + phone + "]";
	}

	
}
