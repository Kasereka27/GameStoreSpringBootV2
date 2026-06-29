package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.exception.UserNotFoundException;
import com.examen.gamestore.repository.UserRepository;
import com.examen.gamestore.service.AdminUserService;
import com.examen.gamestore.web.dto.request.UserAdminForm;

@Service
public class AdminUserServiceImpl implements AdminUserService {

	private static final int PAGE_SIZE = 20;

	private final UserRepository userRepository;

	public AdminUserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public List<User> listUsers(String query, UserRole role, Boolean enabled, int page, int pageSize) {
		int size = pageSize > 0 ? pageSize : PAGE_SIZE;
		int safePage = Math.max(page, 1);
		int offset = (safePage - 1) * size;
		return userRepository.findAll(query, role, enabled, size, offset);
	}

	@Override
	public long countUsers(String query, UserRole role, Boolean enabled) {
		return userRepository.countAll(query, role, enabled);
	}

	@Override
	public User getUser(UUID id) {
		return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
	}

	@Override
	@Transactional
	public void updateUser(UUID targetId, UserAdminForm form, UUID currentAdminId) {
		User target = getUser(targetId);

		if (targetId.equals(currentAdminId) && !form.isEnabled()) {
			throw new IllegalStateException("Vous ne pouvez pas désactiver votre propre compte.");
		}

		boolean demotingAdmin = target.getRole() == UserRole.ROLE_ADMIN
				&& form.getRole() != UserRole.ROLE_ADMIN
				&& form.getRole() != UserRole.ROLE_SUPERADMIN;
		if (demotingAdmin && userRepository.countByRole(UserRole.ROLE_ADMIN) <= 1
				&& target.getRole() == UserRole.ROLE_ADMIN) {
			throw new IllegalStateException("Impossible de rétrograder le dernier administrateur actif.");
		}

		if (targetId.equals(currentAdminId) && form.getRole() == UserRole.ROLE_USER) {
			throw new IllegalStateException("Vous ne pouvez pas rétrograder votre propre rôle.");
		}

		userRepository.updateAdminFields(targetId, form.getRole(), form.isEnabled(), form.isEmailVerified());
	}
}
