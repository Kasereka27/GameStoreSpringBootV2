package com.examen.gamestore.service;

import java.util.UUID;

import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.web.dto.request.ChangePasswordForm;
import com.examen.gamestore.web.dto.request.ProfileForm;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.dto.request.ResetPasswordForm;

public interface UserService {

	UUID register(RegisterForm form);

	User getById(UUID id);

	User getByEmail(String email);

	void updateProfile(UUID userId, ProfileForm form);

	void changePassword(UUID userId, ChangePasswordForm form);

	void requestPasswordReset(String email);

	void resetPassword(ResetPasswordForm form);
}
