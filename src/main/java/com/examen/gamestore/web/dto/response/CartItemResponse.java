package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartItemResponse(
		UUID id,
		UUID gameId,
		String title,
		String slug,
		String coverImageUrl,
		String platform,
		BigDecimal unitPrice,
		int quantity,
		BigDecimal lineTotal
) {
}
