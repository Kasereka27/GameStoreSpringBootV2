package com.examen.gamestore.domain.enums;

public enum Platform {
	PC,
	PS5,
	XBOX,
	SWITCH;

	public String getDisplayLabel() {
		return switch (this) {
			case PC -> "PC";
			case PS5 -> "PS5";
			case XBOX -> "Xbox";
			case SWITCH -> "Switch";
		};
	}

	public static Platform fromString(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Platform requise");
		}
		return Platform.valueOf(value.toUpperCase());
	}
}
