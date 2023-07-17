package com.example.courseselection.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServiceErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InternalServiceErrorException(String message) {
		super(message);
	}

}
