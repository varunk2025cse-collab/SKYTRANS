package com.example.SKYTRANS.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, String>> handleBusiness(BusinessException exception) {
		return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
	}

	// Example message: "batteryLevel must be less than or equal to 100.0"
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
		FieldError error = exception.getBindingResult().getFieldErrors().get(0);
		return ResponseEntity.badRequest().body(Map.of("message", error.getField() + " " + error.getDefaultMessage()));
	}

	// Broken JSON, text in a number field, or an unknown status value
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException exception) {
		return ResponseEntity.badRequest().body(Map.of("message", "The request data is invalid or has a wrong value type"));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDuplicate(DataIntegrityViolationException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Duplicate or invalid database value"));
	}
}
