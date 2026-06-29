package com.examen.gamestore.service.cart;

import com.examen.gamestore.service.impl.CartServiceImpl;

import jakarta.servlet.http.HttpSession;

public class HttpSessionCartScope implements CartScope {

	private final HttpSession session;

	public HttpSessionCartScope(HttpSession session) {
		this.session = session;
	}

	@Override
	public String getOrCreateGuestSessionId() {
		String sessionId = (String) session.getAttribute(CartServiceImpl.CART_SESSION_ID);
		if (sessionId == null) {
			sessionId = java.util.UUID.randomUUID().toString();
			session.setAttribute(CartServiceImpl.CART_SESSION_ID, sessionId);
		}
		return sessionId;
	}

	@Override
	public String getPromoCode() {
		Object value = session.getAttribute(CartServiceImpl.CART_PROMO_CODE);
		return value != null ? value.toString() : null;
	}

	@Override
	public void setPromoCode(String code) {
		session.setAttribute(CartServiceImpl.CART_PROMO_CODE, code);
	}

	@Override
	public void clearPromoCode() {
		session.removeAttribute(CartServiceImpl.CART_PROMO_CODE);
	}

	@Override
	public boolean isGuestCartMerged() {
		return Boolean.TRUE.equals(session.getAttribute(CartServiceImpl.CART_MERGED));
	}

	@Override
	public void markGuestCartMerged() {
		session.setAttribute(CartServiceImpl.CART_MERGED, true);
	}
}
