package com.examen.gamestore.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.examen.gamestore.domain.enums.OrderStatus;

public class LibraryGameView {

	private UUID gameId;
	private String title;
	private String slug;
	private String coverImageUrl;
	private String licenseKey;
	private LocalDateTime purchasedAt;
	private String orderNumber;

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	public LocalDateTime getPurchasedAt() {
		return purchasedAt;
	}

	public void setPurchasedAt(LocalDateTime purchasedAt) {
		this.purchasedAt = purchasedAt;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
}
