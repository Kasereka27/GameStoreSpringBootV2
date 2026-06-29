package com.examen.gamestore.web.dto.response;

import java.time.Instant;

public record ApiErrorResponse(
		int status,
		String error,
		String message,
		Instant timestamp,
		String path
) {
	public static ApiErrorResponse of(int status, String error, String message, String path) {
		return new ApiErrorResponse(status, error, message, Instant.now(), path);
	}
}
