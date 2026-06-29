package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSummaryResponse(
		UUID id,
		String orderNumber,
		String status,
		BigDecimal totalAmount,
		int itemCount,
		LocalDateTime createdAt
) {
}
