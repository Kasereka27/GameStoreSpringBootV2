package com.examen.gamestore.web.dto;

import java.math.BigDecimal;

public class AdminDashboardStats {

	private BigDecimal dailyRevenue = BigDecimal.ZERO;
	private long dailyOrders;
	private long newUsersToday;
	private int lowStockGames;
	private long pendingOrdersCount;

	public BigDecimal getDailyRevenue() {
		return dailyRevenue;
	}

	public void setDailyRevenue(BigDecimal dailyRevenue) {
		this.dailyRevenue = dailyRevenue;
	}

	public long getDailyOrders() {
		return dailyOrders;
	}

	public void setDailyOrders(long dailyOrders) {
		this.dailyOrders = dailyOrders;
	}

	public long getNewUsersToday() {
		return newUsersToday;
	}

	public void setNewUsersToday(long newUsersToday) {
		this.newUsersToday = newUsersToday;
	}

	public int getLowStockGames() {
		return lowStockGames;
	}

	public void setLowStockGames(int lowStockGames) {
		this.lowStockGames = lowStockGames;
	}

	public long getPendingOrdersCount() {
		return pendingOrdersCount;
	}

	public void setPendingOrdersCount(long pendingOrdersCount) {
		this.pendingOrdersCount = pendingOrdersCount;
	}
}
