package com.examen.gamestore.domain.enums;

public enum OrderStatus {
	PENDING,
	PAID,
	PROCESSING,
	COMPLETED,
	CANCELLED,
	REFUNDED,
	FAILED;

	public static OrderStatus fromString(String value) {
		if (value == null) {
			return PENDING;
		}
		return OrderStatus.valueOf(value);
	}
}
