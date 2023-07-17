package com.example.courseselection.vm;

import java.time.Instant;
import java.util.Set;

import com.example.courseselection.models.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserVM extends UserDto {
	public final static int PASSWORD_MIN_LENGTH = 4;
	
	public final static int PASSWORD_MAX_LENGTH = 100;
	
	@Size(min=PASSWORD_MIN_LENGTH, max=PASSWORD_MAX_LENGTH)
	private String password;
	
	public String toString() {
		return "UserVM {" + super.toString() + "}";
	}
}
