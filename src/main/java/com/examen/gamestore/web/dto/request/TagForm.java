package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagForm {

	@NotBlank
	@Size(max = 100)
	private String label;

	@Size(max = 100)
	private String slug;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label != null ? label.trim() : null;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug != null ? slug.trim().toLowerCase() : null;
	}
}
