package com.examen.gamestore.web.mapper;

import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.web.dto.request.ProfileForm;
import com.examen.gamestore.web.dto.request.RegisterForm;

@Component
public class UserMapper {

	public User toNewUser(RegisterForm form, String passwordHash, boolean emailVerified) {
		var user = new User();
		user.setEmail(normalizeEmail(form.getEmail()));
		user.setFirstName(trim(form.getFirstName()));
		user.setLastName(trim(form.getLastName()));
		user.setPasswordHash(passwordHash);
		user.setRole(UserRole.ROLE_USER);
		user.setEnabled(true);
		user.setEmailVerified(emailVerified);
		return user;
	}

	public ProfileForm toProfileForm(User user) {
		var form = new ProfileForm();
		form.setFirstName(user.getFirstName());
		form.setLastName(user.getLastName());
		form.setEmail(user.getEmail());
		return form;
	}

	public ProfileUpdate toProfileUpdate(ProfileForm form) {
		return new ProfileUpdate(trim(form.getFirstName()), trim(form.getLastName()), normalizeEmail(form.getEmail()));
	}

	public record ProfileUpdate(String firstName, String lastName, String email) {
	}

	private String trim(String value) {
		return value != null ? value.trim() : null;
	}

	private String normalizeEmail(String email) {
		return email != null ? email.toLowerCase().trim() : null;
	}
}
