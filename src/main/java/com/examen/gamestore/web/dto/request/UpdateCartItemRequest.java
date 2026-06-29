package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class UpdateCartItemRequest {

	@Min(value = 1, message = "Quantité minimale : 1.")
	@Max(value = 10, message = "Quantité maximale : 10.")
	private int quantity;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
