package com.examen.gamestore.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.domain.model.PasswordResetToken;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.exception.EmailAlreadyExistsException;
import com.examen.gamestore.exception.InvalidTokenException;
import com.examen.gamestore.exception.UserNotFoundException;
import com.examen.gamestore.infrastructure.email.EmailService;
import com.examen.gamestore.repository.PasswordResetTokenRepository;
import com.examen.gamestore.repository.UserRepository;
import com.examen.gamestore.service.UserService;
import com.examen.gamestore.web.dto.request.ChangePasswordForm;
import com.examen.gamestore.web.dto.request.ProfileForm;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.dto.request.ResetPasswordForm;
import com.examen.gamestore.web.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final UserMapper userMapper;
	private final boolean autoVerifyEmail;
	private final String baseUrl;

	public UserServiceImpl(
			UserRepository userRepository,
			PasswordResetTokenRepository tokenRepository,
			PasswordEncoder passwordEncoder,
			EmailService emailService,
			UserMapper userMapper,
			@Value("${app.auth.auto-verify-email:true}") boolean autoVerifyEmail,
			@Value("${app.base-url:http://localhost:8083}") String baseUrl) {
		this.userRepository = userRepository;
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
		this.userMapper = userMapper;
		this.autoVerifyEmail = autoVerifyEmail;
		this.baseUrl = baseUrl;
	}

	@Override
	@Transactional
	public UUID register(RegisterForm form) {
		if (userRepository.existsByEmail(form.getEmail())) {
			throw new EmailAlreadyExistsException(form.getEmail());
		}

		User user = userMapper.toNewUser(form, passwordEncoder.encode(form.getPassword()), autoVerifyEmail);

		UUID id = userRepository.insert(user);
		emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
		return id;
	}

	@Override
	public User getByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(UserNotFoundException::new);
	}

	@Override
	public User getById(UUID id) {
		return userRepository.findById(id)
				.orElseThrow(UserNotFoundException::new);
	}

	@Override
	@Transactional
	public void updateProfile(UUID userId, ProfileForm form) {
		var existing = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (!existing.getEmail().equalsIgnoreCase(form.getEmail())
				&& userRepository.existsByEmail(form.getEmail())) {
			throw new EmailAlreadyExistsException(form.getEmail());
		}
		var update = userMapper.toProfileUpdate(form);
		userRepository.updateProfile(userId, update.firstName(), update.lastName(), update.email());
	}

	@Override
	@Transactional
	public void changePassword(UUID userId, ChangePasswordForm form) {
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Mot de passe actuel incorrect.");
		}
		userRepository.updatePassword(userId, passwordEncoder.encode(form.getNewPassword()));
	}

	@Override
	@Transactional
	public void requestPasswordReset(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {
			tokenRepository.invalidateAllForUser(user.getId());
			String tokenValue = UUID.randomUUID().toString().replace("-", "");
			PasswordResetToken token = new PasswordResetToken();
			token.setUserId(user.getId());
			token.setToken(tokenValue);
			token.setExpiresAt(LocalDateTime.now().plusHours(24));
			tokenRepository.save(token);
			String resetUrl = baseUrl + "/reinitialisation-mot-de-passe?token=" + tokenValue;
			emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
		});
	}

	@Override
	@Transactional
	public void resetPassword(ResetPasswordForm form) {
		PasswordResetToken token = tokenRepository.findValidByToken(form.getToken())
				.orElseThrow(InvalidTokenException::new);
		userRepository.updatePassword(token.getUserId(), passwordEncoder.encode(form.getPassword()));
		tokenRepository.markUsed(token.getId());
	}

	@Override
	@Transactional
	public void deactivateAccount(UUID userId) {
		userRepository.setEnabled(userId, false);
	}
}
