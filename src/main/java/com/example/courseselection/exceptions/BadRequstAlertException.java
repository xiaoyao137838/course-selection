package com.example.courseselection.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.BAD_REQUEST)
public class BadRequstAlertException extends RuntimeException {

	public BadRequstAlertException(String message) {
		super(message);
	}

}
