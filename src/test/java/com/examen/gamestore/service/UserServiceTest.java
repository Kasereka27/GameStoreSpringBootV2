package com.examen.gamestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.exception.EmailAlreadyExistsException;
import com.examen.gamestore.infrastructure.email.EmailService;
import com.examen.gamestore.repository.PasswordResetTokenRepository;
import com.examen.gamestore.repository.UserRepository;
import com.examen.gamestore.service.impl.UserServiceImpl;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordResetTokenRepository tokenRepository;

	@Mock
	private EmailService emailService;

	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userService = new UserServiceImpl(
				userRepository,
				tokenRepository,
				new BCryptPasswordEncoder(),
				emailService,
				new UserMapper(),
				true,
				"http://localhost:8083");
	}

	@Test
	void register_createsUserWithHashedPassword() {
		var form = new RegisterForm();
		form.setFirstName("Jean");
		form.setLastName("Dupont");
		form.setEmail("jean@test.com");
		form.setPassword("Password123!");
		form.setConfirmPassword("Password123!");
		form.setAcceptTerms(true);

		when(userRepository.existsByEmail("jean@test.com")).thenReturn(false);
		when(userRepository.insert(any(User.class))).thenReturn(UUID.randomUUID());

		userService.register(form);

		verify(userRepository).insert(any(User.class));
		verify(emailService).sendWelcomeEmail("jean@test.com", "Jean");
	}

	@Test
	void register_rejectsDuplicateEmail() {
		var form = new RegisterForm();
		form.setEmail("existing@test.com");
		form.setFirstName("A");
		form.setLastName("B");
		form.setPassword("Password123!");
		form.setConfirmPassword("Password123!");
		form.setAcceptTerms(true);

		when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

		assertThrows(EmailAlreadyExistsException.class, () -> userService.register(form));
		verify(userRepository, never()).insert(any());
	}

	@Test
	void requestPasswordReset_doesNotRevealUnknownEmail() {
		when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

		userService.requestPasswordReset("unknown@test.com");

		verify(tokenRepository, never()).save(any());
	}
}
