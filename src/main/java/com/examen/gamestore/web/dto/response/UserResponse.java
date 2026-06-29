package com.examen.gamestore.web.dto.response;

import java.util.UUID;

public record UserResponse(
		UUID id,
		String email,
		String firstName,
		String lastName,
		String role,
		boolean emailVerified
) {
}
