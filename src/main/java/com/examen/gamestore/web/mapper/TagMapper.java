package com.examen.gamestore.web.mapper;

import org.springframework.stereotype.Component;

import com.examen.gamestore.util.SlugUtils;
import com.examen.gamestore.web.dto.TagForm;

@Component
public class TagMapper {

	public String resolveSlug(TagForm form) {
		if (form.getSlug() != null && !form.getSlug().isBlank()) {
			return form.getSlug();
		}
		return SlugUtils.toSlug(form.getLabel());
	}
}
