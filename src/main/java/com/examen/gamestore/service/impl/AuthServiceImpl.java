package com.examen.gamestore.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.RefreshToken;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.exception.InvalidTokenException;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetailsService;
import com.examen.gamestore.infrastructure.security.JwtService;
import com.examen.gamestore.repository.RefreshTokenRepository;
import com.examen.gamestore.service.AuthService;
import com.examen.gamestore.service.UserService;
import com.examen.gamestore.util.TokenHashUtil;
import com.examen.gamestore.web.dto.request.ForgotPasswordForm;
import com.examen.gamestore.web.dto.request.LoginRequest;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.dto.request.ResetPasswordForm;
import com.examen.gamestore.web.dto.response.AuthResponse;
import com.examen.gamestore.web.dto.response.UserResponse;
import com.examen.gamestore.web.mapper.GameApiMapper;

import io.jsonwebtoken.Claims;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserService userService;
	private final GameStoreUserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final GameApiMapper gameApiMapper;

	public AuthServiceImpl(
			UserService userService,
			GameStoreUserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			RefreshTokenRepository refreshTokenRepository,
			GameApiMapper gameApiMapper) {
		this.userService = userService;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenRepository = refreshTokenRepository;
		this.gameApiMapper = gameApiMapper;
	}

	@Override
	@Transactional
	public UserResponse register(RegisterForm form) {
		UUID userId = userService.register(form);
		return gameApiMapper.toUserResponse(userService.getById(userId));
	}

	@Override
	@Transactional
	public AuthResponse login(LoginRequest request) {
		var userDetails = (GameStoreUserDetails) userDetailsService.loadUserByUsername(request.getEmail());
		if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
			throw new BadCredentialsException("Identifiants invalides.");
		}
		return issueTokenPair(userDetails.getUser());
	}

	@Override
	@Transactional
	public AuthResponse refresh(String refreshToken) {
		Claims claims = jwtService.parseToken(refreshToken);
		if (!jwtService.isRefreshToken(claims)) {
			throw new InvalidTokenException();
		}

		String tokenHash = TokenHashUtil.sha256(refreshToken);
		RefreshToken stored = refreshTokenRepository.findActiveByTokenHash(tokenHash)
				.orElseThrow(InvalidTokenException::new);

		if (!stored.getId().equals(jwtService.getTokenId(claims))
				|| !stored.getUserId().equals(jwtService.getUserId(claims))) {
			throw new InvalidTokenException();
		}

		refreshTokenRepository.revokeById(stored.getId());
		User user = userService.getById(stored.getUserId());
		return issueTokenPair(user);
	}

	@Override
	@Transactional
	public void logout(String refreshToken) {
		Claims claims = jwtService.parseToken(refreshToken);
		if (!jwtService.isRefreshToken(claims)) {
			throw new InvalidTokenException();
		}
		refreshTokenRepository.revokeByTokenHash(TokenHashUtil.sha256(refreshToken));
	}

	@Override
	@Transactional
	public void forgotPassword(ForgotPasswordForm form) {
		userService.requestPasswordReset(form.getEmail());
	}

	@Override
	@Transactional
	public void resetPassword(ResetPasswordForm form) {
		userService.resetPassword(form);
	}

	private AuthResponse issueTokenPair(User user) {
		UUID tokenId = UUID.randomUUID();
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user, tokenId);
		LocalDateTime expiresAt = LocalDateTime.now().plus(jwtService.getRefreshTokenExpiration());
		refreshTokenRepository.save(tokenId, user.getId(), TokenHashUtil.sha256(refreshToken), expiresAt);
		return new AuthResponse(accessToken, refreshToken, jwtService.getAccessTokenExpiresInSeconds());
	}
}
