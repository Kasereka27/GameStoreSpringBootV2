package com.examen.gamestore.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.PegiRating;
import com.examen.gamestore.domain.enums.Platform;

public class Game {

	private UUID id;
	private String title;
	private String slug;
	private String shortDescription;
	private String longDescription;
	private String publisher;
	private String developer;
	private LocalDate releaseDate;
	private BigDecimal basePrice;
	private BigDecimal discountedPrice;
	private LocalDateTime discountEndDate;
	private Platform platform;
	private PegiRating pegiRating;
	private GameStatus status;
	private String trailerUrl;
	private String coverImageUrl;
	private String minSpecs;
	private String recommendedSpecs;
	private String supportedLanguages;
	private BigDecimal averageRating;
	private int reviewCount;
	private boolean featured;
	private boolean bestseller;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<String> genreLabels = new ArrayList<>();
	private List<String> tagLabels = new ArrayList<>();
	private List<GameImage> images = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public LocalDateTime getDiscountEndDate() {
		return discountEndDate;
	}

	public void setDiscountEndDate(LocalDateTime discountEndDate) {
		this.discountEndDate = discountEndDate;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public PegiRating getPegiRating() {
		return pegiRating;
	}

	public void setPegiRating(PegiRating pegiRating) {
		this.pegiRating = pegiRating;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public String getTrailerUrl() {
		return trailerUrl;
	}

	public void setTrailerUrl(String trailerUrl) {
		this.trailerUrl = trailerUrl;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
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

	public BigDecimal getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(BigDecimal averageRating) {
		this.averageRating = averageRating;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<String> getGenreLabels() {
		return genreLabels;
	}

	public void setGenreLabels(List<String> genreLabels) {
		this.genreLabels = genreLabels;
	}

	public List<String> getTagLabels() {
		return tagLabels;
	}

	public void setTagLabels(List<String> tagLabels) {
		this.tagLabels = tagLabels;
	}

	public List<GameImage> getImages() {
		return images;
	}

	public void setImages(List<GameImage> images) {
		this.images = images;
	}

	public BigDecimal getEffectivePrice() {
		if (discountedPrice != null && (discountEndDate == null || discountEndDate.isAfter(LocalDateTime.now()))) {
			return discountedPrice;
		}
		return basePrice;
	}

	public boolean isOnPromotion() {
		return discountedPrice != null
				&& discountedPrice.compareTo(basePrice) < 0
				&& (discountEndDate == null || discountEndDate.isAfter(LocalDateTime.now()));
	}

	public int getDiscountPercent() {
		if (!isOnPromotion()) {
			return 0;
		}
		BigDecimal discount = basePrice.subtract(discountedPrice)
				.multiply(BigDecimal.valueOf(100))
				.divide(basePrice, 0, java.math.RoundingMode.HALF_UP);
		return discount.intValue();
	}
}
