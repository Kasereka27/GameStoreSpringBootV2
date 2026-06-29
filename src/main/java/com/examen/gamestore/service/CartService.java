package com.examen.gamestore.service;

import java.util.UUID;

import com.examen.gamestore.domain.model.PromoCode;
import com.examen.gamestore.service.cart.CartScope;
import com.examen.gamestore.web.dto.CartView;

public interface CartService {

	CartView getCart(CartScope scope, UUID userId);

	int getItemCount(CartScope scope, UUID userId);

	void addGame(UUID gameId, CartScope scope, UUID userId);

	void updateQuantity(UUID itemId, int quantity, CartScope scope, UUID userId);

	void removeItem(UUID itemId, CartScope scope, UUID userId);

	void applyPromoCode(String code, CartScope scope, UUID userId);

	void clearPromoCode(CartScope scope);

	void clearCart(CartScope scope, UUID userId);

	PromoCode resolvePromo(CartScope scope);
}
