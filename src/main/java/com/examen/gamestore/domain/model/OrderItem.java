package com.examen.gamestore.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

	private UUID id;
	private UUID orderId;
	private UUID gameId;
	private UUID licenseKeyId;
	private BigDecimal unitPrice;
	private int quantity;
	private Game game;
	private LicenseKey licenseKey;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public UUID getLicenseKeyId() {
		return licenseKeyId;
	}

	public void setLicenseKeyId(UUID licenseKeyId) {
		this.licenseKeyId = licenseKeyId;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public LicenseKey getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(LicenseKey licenseKey) {
		this.licenseKey = licenseKey;
	}

	public BigDecimal getLineTotal() {
		return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}
}
