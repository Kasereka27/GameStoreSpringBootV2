package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PromoForm {

	@NotBlank(message = "Saisissez un code promo")
	@Size(max = 50)
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code != null ? code.trim().toUpperCase() : null;
	}
}
