package com.examen.gamestore.web.mapper;

import org.springframework.stereotype.Component;

import com.examen.gamestore.util.SlugUtils;
import com.examen.gamestore.web.dto.GenreForm;

@Component
public class GenreMapper {

	public String resolveSlug(GenreForm form) {
		if (form.getSlug() != null && !form.getSlug().isBlank()) {
			return form.getSlug();
		}
		return SlugUtils.toSlug(form.getLabel());
	}
}
