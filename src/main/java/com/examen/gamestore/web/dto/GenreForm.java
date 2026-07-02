package com.examen.gamestore.web.dto;

import jakarta.validation.constraints.NotBlank;

public class GenreForm {

	@NotBlank
	private String label;

	private String slug;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
}
