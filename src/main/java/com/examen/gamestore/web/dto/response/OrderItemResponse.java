package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
		UUID id,
		UUID gameId,
		String gameTitle,
		String gameSlug,
		BigDecimal unitPrice,
		int quantity,
		String licenseKey
) {
}
