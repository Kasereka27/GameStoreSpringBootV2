package com.examen.gamestore.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.model.CartItem;
import com.examen.gamestore.domain.model.PromoCode;
import com.examen.gamestore.exception.GameNotFoundException;
import com.examen.gamestore.exception.InsufficientStockException;
import com.examen.gamestore.exception.InvalidPromoCodeException;
import com.examen.gamestore.repository.CartRepository;
import com.examen.gamestore.repository.GameRepository;
import com.examen.gamestore.repository.LicenseKeyRepository;
import com.examen.gamestore.repository.PromoCodeRepository;
import com.examen.gamestore.service.CartService;
import com.examen.gamestore.service.cart.CartScope;
import com.examen.gamestore.web.dto.CartItemView;
import com.examen.gamestore.web.dto.CartView;

@Service
public class CartServiceImpl implements CartService {

	public static final String CART_SESSION_ID = "cartSessionId";
	public static final String CART_PROMO_CODE = "cartPromoCode";
	public static final String CART_MERGED = "cartMerged";

	private final CartRepository cartRepository;
	private final GameRepository gameRepository;
	private final PromoCodeRepository promoCodeRepository;
	private final LicenseKeyRepository licenseKeyRepository;

	public CartServiceImpl(
			CartRepository cartRepository,
			GameRepository gameRepository,
			PromoCodeRepository promoCodeRepository,
			LicenseKeyRepository licenseKeyRepository) {
		this.cartRepository = cartRepository;
		this.gameRepository = gameRepository;
		this.promoCodeRepository = promoCodeRepository;
		this.licenseKeyRepository = licenseKeyRepository;
	}

	@Override
	public CartView getCart(CartScope scope, UUID userId) {
		mergeGuestCartIfNeeded(scope, userId);
		List<CartItem> items = loadItems(scope, userId);
		return buildCartView(items, scope.getPromoCode());
	}

	@Override
	public int getItemCount(CartScope scope, UUID userId) {
		mergeGuestCartIfNeeded(scope, userId);
		if (userId != null) {
			return cartRepository.countByUserId(userId);
		}
		return cartRepository.countBySessionId(scope.getOrCreateGuestSessionId());
	}

	@Override
	@Transactional
	public void addGame(UUID gameId, CartScope scope, UUID userId) {
		var game = gameRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException(gameId.toString()));
		if (game.getStatus() != GameStatus.ACTIVE) {
			throw new IllegalArgumentException("Ce jeu n'est pas disponible à l'achat.");
		}
		if (licenseKeyRepository.countAvailableByGameId(gameId) < 1) {
			throw new InsufficientStockException(game.getTitle());
		}

		mergeGuestCartIfNeeded(scope, userId);

		if (userId != null) {
			addOrIncrement(userId, null, gameId);
		}
		else {
			addOrIncrement(null, scope.getOrCreateGuestSessionId(), gameId);
		}
	}

	@Override
	@Transactional
	public void updateQuantity(UUID itemId, int quantity, CartScope scope, UUID userId) {
		if (quantity < 1 || quantity > 10) {
			throw new IllegalArgumentException("Quantité invalide.");
		}
		var item = cartRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Article introuvable."));
		assertOwnership(item, scope, userId);

		if (licenseKeyRepository.countAvailableByGameId(item.getGameId()) < quantity) {
			throw new InsufficientStockException(item.getGame().getTitle());
		}
		cartRepository.updateQuantity(itemId, quantity);
	}

	@Override
	@Transactional
	public void removeItem(UUID itemId, CartScope scope, UUID userId) {
		var item = cartRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Article introuvable."));
		assertOwnership(item, scope, userId);
		cartRepository.deleteById(itemId);
	}

	@Override
	public void applyPromoCode(String code, CartScope scope, UUID userId) {
		PromoCode promo = promoCodeRepository.findByCode(code)
				.orElseThrow(() -> new InvalidPromoCodeException("Code promo invalide."));
		if (!promo.isValidNow()) {
			throw new InvalidPromoCodeException("Ce code promo n'est plus valide.");
		}
		CartView cart = getCart(scope, userId);
		if (promo.calculateDiscount(cart.getSubtotal()).compareTo(BigDecimal.ZERO) <= 0
				&& cart.getSubtotal().compareTo(BigDecimal.ZERO) > 0
				&& promo.getMinOrderAmount() != null) {
			throw new InvalidPromoCodeException(
					"Montant minimum requis : " + promo.getMinOrderAmount() + " €");
		}
		scope.setPromoCode(promo.getCode());
	}

	@Override
	public void clearPromoCode(CartScope scope) {
		scope.clearPromoCode();
	}

	@Override
	public PromoCode resolvePromo(CartScope scope) {
		String code = scope.getPromoCode();
		if (code == null || code.isBlank()) {
			return null;
		}
		return promoCodeRepository.findByCode(code).filter(PromoCode::isValidNow).orElse(null);
	}

	@Override
	public void clearCart(CartScope scope, UUID userId) {
		if (userId != null) {
			cartRepository.clearByUserId(userId);
		}
		else {
			cartRepository.clearBySessionId(scope.getOrCreateGuestSessionId());
		}
		clearPromoCode(scope);
	}

	private void addOrIncrement(UUID userId, String sessionId, UUID gameId) {
		List<CartItem> items = userId != null
				? cartRepository.findByUserId(userId)
				: cartRepository.findBySessionId(sessionId);

		var existing = items.stream().filter(i -> i.getGameId().equals(gameId)).findFirst();
		if (existing.isPresent()) {
			int newQty = Math.min(10, existing.get().getQuantity() + 1);
			if (licenseKeyRepository.countAvailableByGameId(gameId) < newQty) {
				throw new InsufficientStockException(existing.get().getGame().getTitle());
			}
			cartRepository.updateQuantity(existing.get().getId(), newQty);
		}
		else {
			cartRepository.insert(userId, sessionId, gameId, 1);
		}
	}

	private void mergeGuestCartIfNeeded(CartScope scope, UUID userId) {
		if (userId == null || scope.isGuestCartMerged()) {
			return;
		}
		String guestSessionId = scope.getOrCreateGuestSessionId();
		if (guestSessionId != null) {
			cartRepository.mergeSessionToUser(guestSessionId, userId);
		}
		scope.markGuestCartMerged();
	}

	private List<CartItem> loadItems(CartScope scope, UUID userId) {
		if (userId != null) {
			return cartRepository.findByUserId(userId);
		}
		return cartRepository.findBySessionId(scope.getOrCreateGuestSessionId());
	}

	private CartView buildCartView(List<CartItem> items, String promoCode) {
		var view = new CartView();
		BigDecimal subtotal = BigDecimal.ZERO;
		int count = 0;

		for (CartItem item : items) {
			var itemView = new CartItemView();
			itemView.setId(item.getId());
			itemView.setGameId(item.getGameId());
			itemView.setTitle(item.getGame().getTitle());
			itemView.setSlug(item.getGame().getSlug());
			itemView.setCoverImageUrl(item.getGame().getCoverImageUrl());
			itemView.setPlatform(item.getGame().getPlatform());
			itemView.setUnitPrice(item.getGame().getEffectivePrice());
			itemView.setQuantity(item.getQuantity());
			view.getItems().add(itemView);
			subtotal = subtotal.add(itemView.getLineTotal());
			count += item.getQuantity();
		}

		view.setSubtotal(subtotal);
		view.setItemCount(count);
		view.setAppliedPromoCode(promoCode);

		final BigDecimal cartSubtotal = subtotal;
		BigDecimal discount = BigDecimal.ZERO;
		if (promoCode != null && !promoCode.isBlank()) {
			discount = promoCodeRepository.findByCode(promoCode)
					.filter(PromoCode::isValidNow)
					.map(promo -> promo.calculateDiscount(cartSubtotal))
					.orElse(BigDecimal.ZERO);
		}

		view.setDiscount(discount);
		view.setTotal(subtotal.subtract(discount).max(BigDecimal.ZERO));
		return view;
	}

	private void assertOwnership(CartItem item, CartScope scope, UUID userId) {
		if (userId != null) {
			if (item.getUserId() == null || !item.getUserId().equals(userId)) {
				throw new IllegalArgumentException("Accès refusé.");
			}
		}
		else if (item.getSessionId() == null || !item.getSessionId().equals(scope.getOrCreateGuestSessionId())) {
			throw new IllegalArgumentException("Accès refusé.");
		}
	}
}
