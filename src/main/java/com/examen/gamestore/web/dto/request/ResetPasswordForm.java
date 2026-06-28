package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordForm {

	@NotBlank
	private String token;

	@NotBlank(message = "Le mot de passe est requis.")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
	private String password;

	@NotBlank(message = "La confirmation est requise.")
	private String confirmPassword;

	@AssertTrue(message = "Les mots de passe ne correspondent pas.")
	public boolean isPasswordMatching() {
		return password != null && password.equals(confirmPassword);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
