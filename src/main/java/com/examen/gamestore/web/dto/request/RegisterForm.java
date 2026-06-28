package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

	@NotBlank(message = "Le prénom est requis.")
	@Size(max = 100)
	private String firstName;

	@NotBlank(message = "Le nom est requis.")
	@Size(max = 100)
	private String lastName;

	@NotBlank(message = "L'e-mail est requis.")
	@Email(message = "Format d'e-mail invalide.")
	private String email;

	@NotBlank(message = "Le mot de passe est requis.")
	@Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
	private String password;

	@NotBlank(message = "La confirmation est requise.")
	private String confirmPassword;

	private boolean acceptTerms;

	@AssertTrue(message = "Les mots de passe ne correspondent pas.")
	public boolean isPasswordMatching() {
		return password != null && password.equals(confirmPassword);
	}

	@AssertTrue(message = "Vous devez accepter les conditions.")
	public boolean isTermsAccepted() {
		return acceptTerms;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public boolean isAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}
}
