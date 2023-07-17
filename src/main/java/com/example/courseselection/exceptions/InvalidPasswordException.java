package com.example.courseselection.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_ACCEPTABLE)
public class InvalidPasswordException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidPasswordException() {
		super("The password is not valid.");
	}

}
