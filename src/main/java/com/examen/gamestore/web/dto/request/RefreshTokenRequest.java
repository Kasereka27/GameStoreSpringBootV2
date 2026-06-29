package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {

	@NotBlank(message = "Le refresh token est requis.")
	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
