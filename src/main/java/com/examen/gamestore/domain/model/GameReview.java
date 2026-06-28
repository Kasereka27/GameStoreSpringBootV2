package com.examen.gamestore.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameReview {

	private UUID id;
	private UUID gameId;
	private UUID userId;
	private int rating;
	private String content;
	private boolean verifiedPurchase;
	private int helpfulCount;
	private LocalDateTime createdAt;
	private String authorFirstName;
	private String authorLastName;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isVerifiedPurchase() {
		return verifiedPurchase;
	}

	public void setVerifiedPurchase(boolean verifiedPurchase) {
		this.verifiedPurchase = verifiedPurchase;
	}

	public int getHelpfulCount() {
		return helpfulCount;
	}

	public void setHelpfulCount(int helpfulCount) {
		this.helpfulCount = helpfulCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	public String getAuthorLastName() {
		return authorLastName;
	}

	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}

	public String getMaskedAuthorName() {
		if (authorFirstName == null || authorFirstName.isBlank()) {
			return "Joueur";
		}
		String initial = authorFirstName.substring(0, 1).toUpperCase();
		return initial + "***" + (authorLastName != null && !authorLastName.isBlank()
				? authorLastName.substring(0, 1).toLowerCase()
				: "");
	}
}
