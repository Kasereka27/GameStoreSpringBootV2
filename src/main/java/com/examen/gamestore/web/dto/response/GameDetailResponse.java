package com.examen.gamestore.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GameDetailResponse(
		UUID id,
		String title,
		String slug,
		String shortDescription,
		String longDescription,
		String publisher,
		String developer,
		LocalDate releaseDate,
		BigDecimal basePrice,
		BigDecimal effectivePrice,
		boolean onPromotion,
		int discountPercent,
		String platform,
		String pegiRating,
		String coverImageUrl,
		String trailerUrl,
		String minSpecs,
		String recommendedSpecs,
		String supportedLanguages,
		BigDecimal averageRating,
		int reviewCount,
		boolean featured,
		boolean bestseller,
		List<String> genres,
		List<String> tags,
		List<GameImageResponse> images
) {
}
