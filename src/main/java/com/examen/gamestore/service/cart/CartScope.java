package com.examen.gamestore.service.cart;

public interface CartScope {

	String getOrCreateGuestSessionId();

	String getPromoCode();

	void setPromoCode(String code);

	void clearPromoCode();

	boolean isGuestCartMerged();

	void markGuestCartMerged();
}
