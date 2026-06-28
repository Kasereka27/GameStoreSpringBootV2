package com.examen.gamestore.web.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.examen.gamestore.domain.enums.DiscountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PromoCodeForm {

	@NotBlank
	@Size(max = 50)
	private String code;

	@NotNull
	private DiscountType discountType = DiscountType.PERCENTAGE;

	@NotNull
	@DecimalMin("0.01")
	private BigDecimal discountValue;

	private BigDecimal minOrderAmount;

	private Integer maxUsages;

	private LocalDateTime expiresAt;

	private boolean active = true;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code != null ? code.trim().toUpperCase() : null;
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
}
