package com.banking.user.exception;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.banking.user.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request)
	{

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException .class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request)
	{
		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + " : "+ error.getDefaultMessage())
				.collect(Collectors.joining(", "));

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message(message)
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request)
	{
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}
	
}
