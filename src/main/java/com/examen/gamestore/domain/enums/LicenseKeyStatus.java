package com.examen.gamestore.domain.enums;

public enum LicenseKeyStatus {
	AVAILABLE,
	SOLD,
	RESERVED,
	INVALID;

	public static LicenseKeyStatus fromString(String value) {
		if (value == null) {
			return AVAILABLE;
		}
		return LicenseKeyStatus.valueOf(value);
	}
}
