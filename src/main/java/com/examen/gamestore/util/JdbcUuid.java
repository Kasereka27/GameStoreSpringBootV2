package com.examen.gamestore.util;

import java.util.UUID;

public final class JdbcUuid {

	private JdbcUuid() {
	}

	public static String toParam(UUID uuid) {
		return uuid != null ? uuid.toString() : null;
	}
}
