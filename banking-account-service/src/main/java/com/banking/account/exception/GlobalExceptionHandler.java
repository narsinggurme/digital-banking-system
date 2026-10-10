package com.banking.account.exception;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.banking.account.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler
{
	private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex, WebRequest request)
	{
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(message)
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request)
	{
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Invalid request body.")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(DuplicateAccountException.class)
	public ResponseEntity<ErrorResponse> DuplicateAccountException(DuplicateAccountException ex, WebRequest request)
	{
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request)
	{
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message("Account violates a database constraint")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request)
	{
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(InvalidAccountStatusTransitionException.class)
	public ResponseEntity<ErrorResponse> handleInvalidAccountStatusTransition(InvalidAccountStatusTransitionException ex, WebRequest request)
	{
		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity .status(HttpStatus.BAD_REQUEST) .body(errorResponse);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,  WebRequest request)
	{
		LOGGER.error("Unexpected error while procession: " + ex);
		ErrorResponse response = ErrorResponse.builder()
				.timestamp(OffsetDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message("An unexpected error occured.")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

}
