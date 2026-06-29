package com.examen.gamestore.web.dto.response;

public record AuthResponse(
		String accessToken,
		String refreshToken,
		String tokenType,
		long expiresIn
) {
	public AuthResponse(String accessToken, String refreshToken, long expiresInSeconds) {
		this(accessToken, refreshToken, "Bearer", expiresInSeconds);
	}
}
