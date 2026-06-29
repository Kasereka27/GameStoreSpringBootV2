package com.examen.gamestore.web.dto.response;

import java.util.List;

public record GamePageResponse(
		List<GameResponse> items,
		long totalResults,
		int page,
		int pageSize,
		int totalPages,
		int from,
		int to
) {
}
