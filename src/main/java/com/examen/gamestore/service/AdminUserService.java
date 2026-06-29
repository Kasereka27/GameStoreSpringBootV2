package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.web.dto.request.UserAdminForm;

public interface AdminUserService {

	List<User> listUsers(String query, UserRole role, Boolean enabled, int page, int pageSize);

	long countUsers(String query, UserRole role, Boolean enabled);

	User getUser(UUID id);

	void updateUser(UUID targetId, UserAdminForm form, UUID currentAdminId);
}
