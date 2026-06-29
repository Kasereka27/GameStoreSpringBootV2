package com.examen.gamestore.web.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class AddCartItemRequest {

	@NotNull(message = "L'identifiant du jeu est requis.")
	private UUID gameId;

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}
}
