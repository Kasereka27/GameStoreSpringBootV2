package com.examen.gamestore.domain.enums;

public enum GameStatus {
	ACTIVE,
	INACTIVE,
	PRE_ORDER;

	public static GameStatus fromString(String value) {
		if (value == null || value.isBlank()) {
			return ACTIVE;
		}
		return GameStatus.valueOf(value.toUpperCase());
	}
}
