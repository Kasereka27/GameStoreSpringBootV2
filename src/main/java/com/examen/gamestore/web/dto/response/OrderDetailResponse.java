package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
		UUID id,
		String orderNumber,
		String status,
		BigDecimal subtotal,
		BigDecimal discountAmount,
		BigDecimal totalAmount,
		String paymentMethod,
		LocalDateTime createdAt,
		List<OrderItemResponse> items
) {
}
