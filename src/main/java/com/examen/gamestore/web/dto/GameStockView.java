package com.examen.gamestore.web.dto;

import java.util.UUID;

public class GameStockView {

	private UUID gameId;
	private String gameTitle;
	private String gameSlug;
	private int availableCount;
	private int soldCount;
	private int totalCount;

	public UUID getGameId() {
		return gameId;
	}

	public void setGameId(UUID gameId) {
		this.gameId = gameId;
	}

	public String getGameTitle() {
		return gameTitle;
	}

	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}

	public String getGameSlug() {
		return gameSlug;
	}

	public void setGameSlug(String gameSlug) {
		this.gameSlug = gameSlug;
	}

	public int getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(int availableCount) {
		this.availableCount = availableCount;
	}

	public int getSoldCount() {
		return soldCount;
	}

	public void setSoldCount(int soldCount) {
		this.soldCount = soldCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isLowStock() {
		return availableCount < 10;
	}
}
