package com.examen.gamestore.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailySalesView {

	private LocalDate date;
	private String dayLabel;
	private BigDecimal revenue = BigDecimal.ZERO;
	private long orderCount;
	private int barHeightPercent;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDayLabel() {
		return dayLabel;
	}

	public void setDayLabel(String dayLabel) {
		this.dayLabel = dayLabel;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}

	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}

	public long getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(long orderCount) {
		this.orderCount = orderCount;
	}

	public int getBarHeightPercent() {
		return barHeightPercent;
	}

	public void setBarHeightPercent(int barHeightPercent) {
		this.barHeightPercent = barHeightPercent;
	}

	public String getRevenueLabel() {
		if (revenue.compareTo(BigDecimal.valueOf(1000)) >= 0) {
			return String.format("%.1fk", revenue.doubleValue() / 1000);
		}
		return revenue.stripTrailingZeros().toPlainString();
	}
}
