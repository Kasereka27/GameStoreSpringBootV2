package com.examen.gamestore.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.examen.gamestore.domain.enums.LicenseKeyStatus;

public class LicenseKey {

	private UUID id;
	private UUID gameId;
	private String keyValue;
	private LicenseKeyStatus status;
	private UUID orderId;
	private LocalDateTime assignedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public LicenseKeyStatus getStatus() {
		return status;
	}

	public void setStatus(LicenseKeyStatus status) {
		this.status = status;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}
}
