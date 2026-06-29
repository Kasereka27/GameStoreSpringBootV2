package com.examen.gamestore.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.examen.gamestore.web.dto.response.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "com.examen.gamestore.web.controller.api")
public class ApiExceptionHandler {

	@ExceptionHandler(GameNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleGameNotFound(
			GameNotFoundException ex, HttpServletRequest request) {
		return error(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleOrderNotFound(
			OrderNotFoundException ex, HttpServletRequest request) {
		return error(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	@ExceptionHandler(EmptyCartException.class)
	public ResponseEntity<ApiErrorResponse> handleEmptyCart(
			EmptyCartException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ApiErrorResponse> handleInsufficientStock(
			InsufficientStockException ex, HttpServletRequest request) {
		return error(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
	}

	@ExceptionHandler(InvalidPromoCodeException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidPromo(
			InvalidPromoCodeException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleUserNotFound(
			UserNotFoundException ex, HttpServletRequest request) {
		return error(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiErrorResponse> handleEmailExists(
			EmailAlreadyExistsException ex, HttpServletRequest request) {
		return error(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ApiErrorResponse> handleInvalidToken(
			InvalidTokenException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
	public ResponseEntity<ApiErrorResponse> handleAuthentication(
			AuthenticationException ex, HttpServletRequest request) {
		return error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Identifiants invalides.", request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
			IllegalArgumentException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalState(
			IllegalStateException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException ex, HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(" "));
		return error(HttpStatus.UNPROCESSABLE_ENTITY, "Validation Failed", message, request);
	}

	private ResponseEntity<ApiErrorResponse> error(
			HttpStatus status, String error, String message, HttpServletRequest request) {
		return ResponseEntity.status(status)
				.body(ApiErrorResponse.of(status.value(), error, message, request.getRequestURI()));
	}
}
