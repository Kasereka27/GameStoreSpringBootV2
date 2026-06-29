package com.examen.gamestore.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record LibraryGameResponse(
		UUID gameId,
		String title,
		String slug,
		String coverImageUrl,
		String licenseKey,
		LocalDateTime purchasedAt,
		String orderNumber
) {
}
