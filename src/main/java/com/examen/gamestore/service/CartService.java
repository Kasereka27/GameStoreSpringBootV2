package com.examen.gamestore.service;

import java.util.UUID;

import com.examen.gamestore.web.dto.CartView;

import jakarta.servlet.http.HttpSession;

public interface CartService {

	CartView getCart(HttpSession session, UUID userId);

	int getItemCount(HttpSession session, UUID userId);

	void addGame(UUID gameId, HttpSession session, UUID userId);

	void updateQuantity(UUID itemId, int quantity, HttpSession session, UUID userId);

	void removeItem(UUID itemId, HttpSession session, UUID userId);

	void applyPromoCode(String code, HttpSession session, UUID userId);

	void clearPromoCode(HttpSession session);

	void clearCart(HttpSession session, UUID userId);

	com.examen.gamestore.domain.model.PromoCode resolvePromo(HttpSession session);
}
