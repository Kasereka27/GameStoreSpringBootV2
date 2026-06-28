package com.examen.gamestore.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartView {

	private List<CartItemView> items = new ArrayList<>();
	private BigDecimal subtotal = BigDecimal.ZERO;
	private BigDecimal discount = BigDecimal.ZERO;
	private BigDecimal total = BigDecimal.ZERO;
	private String appliedPromoCode;
	private int itemCount;

	public List<CartItemView> getItems() {
		return items;
	}

	public void setItems(List<CartItemView> items) {
		this.items = items;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getAppliedPromoCode() {
		return appliedPromoCode;
	}

	public void setAppliedPromoCode(String appliedPromoCode) {
		this.appliedPromoCode = appliedPromoCode;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public boolean isEmpty() {
		return items == null || items.isEmpty();
	}
}
