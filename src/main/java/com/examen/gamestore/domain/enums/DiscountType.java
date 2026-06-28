package com.examen.gamestore.domain.enums;

public enum DiscountType {
	PERCENTAGE,
	FIXED_AMOUNT;

	public static DiscountType fromString(String value) {
		if (value == null) {
			return PERCENTAGE;
		}
		return DiscountType.valueOf(value);
	}
}
