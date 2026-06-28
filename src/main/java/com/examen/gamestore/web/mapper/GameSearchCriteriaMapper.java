package com.examen.gamestore.web.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.web.dto.GameSearchCriteria;

@Component
public class GameSearchCriteriaMapper {

	public GameSearchCriteria fromCatalogueParams(
			String query,
			String[] genres,
			Platform platform,
			BigDecimal priceMin,
			BigDecimal priceMax,
			String sort,
			int page,
			boolean promoOnly) {

		var criteria = new GameSearchCriteria();
		criteria.setQuery(query);
		if (genres != null && genres.length > 0) {
			criteria.setGenres(List.of(genres));
		}
		criteria.setPlatform(platform);
		criteria.setPriceMin(priceMin);
		criteria.setPriceMax(priceMax);
		criteria.setSort(sort);
		criteria.setPage(page);
		criteria.setPromoOnly(promoOnly);
		return criteria;
	}

	public GameSearchCriteria fromAdminParams(String query, Platform platform, GameStatus status, int page) {
		var criteria = new GameSearchCriteria();
		criteria.setQuery(query);
		criteria.setPlatform(platform);
		criteria.setStatus(status);
		criteria.setPage(page);
		criteria.setPageSize(20);
		criteria.setAdminMode(true);
		return criteria;
	}
}
