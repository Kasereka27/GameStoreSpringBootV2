package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GameResponse(
		UUID id,
		String title,
		String slug,
		String shortDescription,
		BigDecimal basePrice,
		BigDecimal effectivePrice,
		boolean onPromotion,
		int discountPercent,
		String platform,
		String coverImageUrl,
		BigDecimal averageRating,
		int reviewCount,
		boolean featured,
		boolean bestseller,
		List<String> genres,
		List<String> tags
) {
}
