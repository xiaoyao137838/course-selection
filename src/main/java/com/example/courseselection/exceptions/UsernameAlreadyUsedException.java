package com.example.courseselection.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.UNPROCESSABLE_ENTITY)
public class UsernameAlreadyUsedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UsernameAlreadyUsedException() {
		super("Username has already been used.");
	}

}
