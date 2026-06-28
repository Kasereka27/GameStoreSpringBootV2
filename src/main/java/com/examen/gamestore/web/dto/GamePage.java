package com.examen.gamestore.web.dto;

import java.util.List;

import com.examen.gamestore.domain.model.Game;

public record GamePage(
		List<Game> games,
		long totalResults,
		int page,
		int pageSize,
		int totalPages,
		int from,
		int to
) {
}
