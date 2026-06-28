package com.examen.gamestore.web.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.PegiRating;
import com.examen.gamestore.domain.enums.Platform;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GameForm {

	@NotBlank
	@Size(max = 255)
	private String title;

	@NotBlank
	@Size(max = 255)
	private String slug;

	@Size(max = 500)
	private String shortDescription;

	private String longDescription;

	@Size(max = 255)
	private String publisher;

	@Size(max = 255)
	private String developer;

	private LocalDate releaseDate;

	@NotNull
	@DecimalMin("0.0")
	private BigDecimal basePrice;

	@DecimalMin("0.0")
	private BigDecimal discountedPrice;

	@NotNull
	private Platform platform;

	private GameStatus status = GameStatus.ACTIVE;

	@Size(max = 500)
	private String coverImageUrl;

	@Size(max = 500)
	private String trailerUrl;

	private PegiRating pegiRating;

	private String minSpecs;
	private String recommendedSpecs;
	private String supportedLanguages;

	private List<String> genreSlugs = new ArrayList<>();
	private List<String> tagSlugs = new ArrayList<>();
	private String screenshotUrls;

	private boolean featured;
	private boolean bestseller;

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

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public BigDecimal getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(BigDecimal discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getTrailerUrl() {
		return trailerUrl;
	}

	public void setTrailerUrl(String trailerUrl) {
		this.trailerUrl = trailerUrl;
	}

	public PegiRating getPegiRating() {
		return pegiRating;
	}

	public void setPegiRating(PegiRating pegiRating) {
		this.pegiRating = pegiRating;
	}

	public String getMinSpecs() {
		return minSpecs;
	}

	public void setMinSpecs(String minSpecs) {
		this.minSpecs = minSpecs;
	}

	public String getRecommendedSpecs() {
		return recommendedSpecs;
	}

	public void setRecommendedSpecs(String recommendedSpecs) {
		this.recommendedSpecs = recommendedSpecs;
	}

	public String getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(String supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public List<String> getGenreSlugs() {
		return genreSlugs;
	}

	public void setGenreSlugs(List<String> genreSlugs) {
		this.genreSlugs = genreSlugs != null ? genreSlugs : new ArrayList<>();
	}

	public List<String> getTagSlugs() {
		return tagSlugs;
	}

	public void setTagSlugs(List<String> tagSlugs) {
		this.tagSlugs = tagSlugs != null ? tagSlugs : new ArrayList<>();
	}

	public String getScreenshotUrls() {
		return screenshotUrls;
	}

	public void setScreenshotUrls(String screenshotUrls) {
		this.screenshotUrls = screenshotUrls;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public boolean isBestseller() {
		return bestseller;
	}

	public void setBestseller(boolean bestseller) {
		this.bestseller = bestseller;
	}
}
