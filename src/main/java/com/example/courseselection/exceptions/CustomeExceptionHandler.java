package com.example.courseselection.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomeExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleAllExceptions
	(WebRequest request, Exception ex) {
		
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				ex.getMessage(),
				request.getDescription(false));
		
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
				
	}
	
	@ExceptionHandler(EmailNotFoundException.class)
	public final ResponseEntity<ErrorDetails> handleNotFoundExceptions
	(WebRequest request, Exception ex) {
		
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				ex.getMessage(),
				request.getDescription(false));
		
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler({EmailAlreadyUsedException.class, 
		UsernameAlreadyUsedException.class})
	public ResponseEntity<ErrorDetails> handleAlreadyUsedExceptions
	(WebRequest request, Exception ex) {
		
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				ex.getMessage(),
				request.getDescription(false));
		
		return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
				
	}
	
	@ExceptionHandler({InvalidPasswordException.class, 
		BadRequstAlertException.class})
	public ResponseEntity<ErrorDetails> invalidRequestExceptions
	(WebRequest request, Exception ex) {
		
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				ex.getMessage(),
				request.getDescription(false));
		
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
				
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, HttpHeaders 
			headers, HttpStatusCode status, WebRequest request) {
		
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(),
				"Total Errors:" + ex.getErrorCount() + " First Error:"
				+ ex.getFieldError().getDefaultMessage(),
				request.getDescription(false));
				
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}

}
