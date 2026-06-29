package com.examen.gamestore.util;

import java.text.Normalizer;

public final class SlugUtils {

	private SlugUtils() {
	}

	public static String toSlug(String label) {
		if (label == null || label.isBlank()) {
			return "";
		}
		String normalized = Normalizer.normalize(label.trim().toLowerCase(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
	}
}
