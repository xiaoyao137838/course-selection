package com.example.courseselection.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailNotFoundException() {
		super("Email is not found.");
		
	}
	
}
