package com.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
		List<String> details = exception.getBindingResult().getFieldErrors().stream().map(this::formatFieldError)
				.toList();

		return error(HttpStatus.BAD_REQUEST, "Validation failed", details);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception) {
		return error(HttpStatus.BAD_REQUEST, "Invalid request body",
				List.of("Check that JSON fields and dates use the expected format (yyyy-MM-dd)."));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception) {
		return error(HttpStatus.BAD_REQUEST, "Invalid request", List.of(exception.getMessage()));
	}

	private String formatFieldError(FieldError error) {
		return error.getField() + ": " + error.getDefaultMessage();
	}

	private ResponseEntity<ApiError> error(HttpStatus status, String message, List<String> details) {
		ApiError body = new ApiError(Instant.now(), status.value(), message, details);
		return ResponseEntity.status(status).body(body);
	}
}
