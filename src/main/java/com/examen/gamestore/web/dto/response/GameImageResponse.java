package com.examen.gamestore.web.dto.response;

import java.util.UUID;

public record GameImageResponse(
		UUID id,
		String url,
		int sortOrder,
		String imageType
) {
}
