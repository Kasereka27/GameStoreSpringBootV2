package com.examen.gamestore.web.dto.request;

import com.examen.gamestore.domain.enums.UserRole;

import jakarta.validation.constraints.NotNull;

public class UserAdminForm {

	@NotNull
	private UserRole role;

	private boolean enabled = true;

	private boolean emailVerified;

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}
}
