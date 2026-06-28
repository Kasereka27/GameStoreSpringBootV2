package com.examen.gamestore.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.examen.gamestore.domain.enums.DiscountType;

public class PromoCode {

	private UUID id;
	private String code;
	private DiscountType discountType;
	private BigDecimal discountValue;
	private BigDecimal minOrderAmount;
	private Integer maxUsages;
	private int usageCount;
	private LocalDateTime expiresAt;
	private boolean active;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DiscountType getDiscountType() {
		return discountType;
	}

	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public BigDecimal getMinOrderAmount() {
		return minOrderAmount;
	}

	public void setMinOrderAmount(BigDecimal minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}

	public Integer getMaxUsages() {
		return maxUsages;
	}

	public void setMaxUsages(Integer maxUsages) {
		this.maxUsages = maxUsages;
	}

	public int getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isValidNow() {
		if (!active) {
			return false;
		}
		if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
			return false;
		}
		if (maxUsages != null && usageCount >= maxUsages) {
			return false;
		}
		return true;
	}

	public BigDecimal calculateDiscount(BigDecimal subtotal) {
		if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}
		if (minOrderAmount != null && subtotal.compareTo(minOrderAmount) < 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal discount = switch (discountType) {
			case PERCENTAGE -> subtotal.multiply(discountValue)
					.divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
			case FIXED_AMOUNT -> discountValue.min(subtotal);
		};
		return discount.max(BigDecimal.ZERO);
	}
}
