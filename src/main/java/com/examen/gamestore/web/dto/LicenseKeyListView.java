package com.examen.gamestore.web.dto;

import java.util.UUID;

import com.examen.gamestore.domain.enums.LicenseKeyStatus;

public class LicenseKeyListView {

	private UUID id;
	private String keyValue;
	private LicenseKeyStatus status;
	private String orderNumber;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
}
