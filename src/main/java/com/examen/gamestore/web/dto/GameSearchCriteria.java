package com.examen.gamestore.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.Platform;

public class GameSearchCriteria {

	private String query;
	private List<String> genres;
	private Platform platform;
	private GameStatus status;
	private BigDecimal priceMin;
	private BigDecimal priceMax;
	private boolean promoOnly;
	private boolean adminMode;
	private String sort = "relevance";
	private int page = 1;
	private int pageSize = 12;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public BigDecimal getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(BigDecimal priceMin) {
		this.priceMin = priceMin;
	}

	public BigDecimal getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(BigDecimal priceMax) {
		this.priceMax = priceMax;
	}

	public boolean isPromoOnly() {
		return promoOnly;
	}

	public void setPromoOnly(boolean promoOnly) {
		this.promoOnly = promoOnly;
	}

	public boolean isAdminMode() {
		return adminMode;
	}

	public void setAdminMode(boolean adminMode) {
		this.adminMode = adminMode;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = Math.max(1, page);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = Math.min(50, Math.max(1, pageSize));
	}

	public int getOffset() {
		return (page - 1) * pageSize;
	}
}
