package com.examen.gamestore.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GameForm {

	@NotBlank
	private String title;

	private String slug;

	private String description;

	@NotNull
	@DecimalMin("0.0")
	private BigDecimal price;

	private String platform;

	private List<String> genreSlugs = new ArrayList<>();

	private List<String> tagSlugs = new ArrayList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public List<String> getGenreSlugs() {
		return genreSlugs;
	}

	public void setGenreSlugs(List<String> genreSlugs) {
		this.genreSlugs = genreSlugs;
	}

	public List<String> getTagSlugs() {
		return tagSlugs;
	}

	public void setTagSlugs(List<String> tagSlugs) {
		this.tagSlugs = tagSlugs;
	}
}
