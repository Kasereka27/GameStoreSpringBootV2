package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordForm {

	@NotBlank(message = "Le mot de passe actuel est requis.")
	private String currentPassword;

	@NotBlank(message = "Le nouveau mot de passe est requis.")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
	private String newPassword;

	@NotBlank(message = "La confirmation est requise.")
	private String confirmPassword;

	@AssertTrue(message = "Les mots de passe ne correspondent pas.")
	public boolean isPasswordMatching() {
		return newPassword != null && newPassword.equals(confirmPassword);
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
