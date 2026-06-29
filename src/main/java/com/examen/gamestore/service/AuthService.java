package com.examen.gamestore.service;

import com.examen.gamestore.web.dto.request.ForgotPasswordForm;
import com.examen.gamestore.web.dto.request.LoginRequest;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.dto.request.ResetPasswordForm;
import com.examen.gamestore.web.dto.response.AuthResponse;
import com.examen.gamestore.web.dto.response.UserResponse;

public interface AuthService {

	UserResponse register(RegisterForm form);

	AuthResponse login(LoginRequest request);

	AuthResponse refresh(String refreshToken);

	void logout(String refreshToken);

	void forgotPassword(ForgotPasswordForm form);

	void resetPassword(ResetPasswordForm form);
}
