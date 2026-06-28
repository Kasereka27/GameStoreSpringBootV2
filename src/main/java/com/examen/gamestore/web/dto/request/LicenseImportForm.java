package com.examen.gamestore.web.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class LicenseImportForm {

	@NotNull
	private UUID gameId;

	private String keysText;

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public String getKeysText() {
		return keysText;
	}

	public void setKeysText(String keysText) {
		this.keysText = keysText;
	}
}
