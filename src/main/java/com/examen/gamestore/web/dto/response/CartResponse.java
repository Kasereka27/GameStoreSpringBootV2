package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
		List<CartItemResponse> items,
		BigDecimal subtotal,
		BigDecimal discount,
		BigDecimal total,
		String appliedPromoCode,
		int itemCount
) {
}
