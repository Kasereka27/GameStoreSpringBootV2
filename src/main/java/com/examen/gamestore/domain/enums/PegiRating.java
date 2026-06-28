package com.examen.gamestore.domain.enums;

public enum PegiRating {
	PEGI_3,
	PEGI_7,
	PEGI_12,
	PEGI_16,
	PEGI_18;

	public String getDisplayLabel() {
		return name().replace("PEGI_", "PEGI ");
	}
}
