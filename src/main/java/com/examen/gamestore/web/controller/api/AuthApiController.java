package com.examen.gamestore.web.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.service.AuthService;
import com.examen.gamestore.web.dto.request.ForgotPasswordForm;
import com.examen.gamestore.web.dto.request.LoginRequest;
import com.examen.gamestore.web.dto.request.RefreshTokenRequest;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.dto.request.ResetPasswordForm;
import com.examen.gamestore.web.dto.response.AuthResponse;
import com.examen.gamestore.web.dto.response.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

	private final AuthService authService;

	public AuthApiController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse register(@Valid @RequestBody RegisterForm form) {
		return authService.register(form);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return authService.refresh(request.getRefreshToken());
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(@Valid @RequestBody RefreshTokenRequest request) {
		authService.logout(request.getRefreshToken());
	}

	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@Valid @RequestBody ForgotPasswordForm form) {
		authService.forgotPassword(form);
	}

	@PostMapping("/reset-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resetPassword(@Valid @RequestBody ResetPasswordForm form) {
		authService.resetPassword(form);
	}

	@GetMapping("/verify-email")
	public ResponseEntity<java.util.Map<String, String>> verifyEmail(@RequestParam String token) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
				.body(java.util.Map.of("message", "Vérification email non implémentée."));
	}
}
