package com.examen.gamestore.web.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.CartService;
import com.examen.gamestore.service.cart.ApiCartScope;
import com.examen.gamestore.service.cart.ApiCartScopeFactory;
import com.examen.gamestore.web.dto.request.AddCartItemRequest;
import com.examen.gamestore.web.dto.request.PromoForm;
import com.examen.gamestore.web.dto.request.UpdateCartItemRequest;
import com.examen.gamestore.web.dto.response.CartResponse;
import com.examen.gamestore.web.mapper.CommerceApiMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

	private final CartService cartService;
	private final ApiCartScopeFactory cartScopeFactory;
	private final CommerceApiMapper commerceApiMapper;

	public CartApiController(
			CartService cartService,
			ApiCartScopeFactory cartScopeFactory,
			CommerceApiMapper commerceApiMapper) {
		this.cartService = cartService;
		this.cartScopeFactory = cartScopeFactory;
		this.commerceApiMapper = commerceApiMapper;
	}

	@GetMapping
	public ResponseEntity<CartResponse> getCart(
			HttpServletRequest request,
			HttpServletResponse response,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		ApiCartScope scope = cartScopeFactory.fromRequest(request, user);
		writeGuestSessionHeader(response, scope, user);
		var cart = cartService.getCart(scope, resolveUserId(user));
		return ResponseEntity.ok(commerceApiMapper.toCartResponse(cart));
	}

	@PostMapping("/items")
	public ResponseEntity<CartResponse> addItem(
			@Valid @RequestBody AddCartItemRequest body,
			HttpServletRequest request,
			HttpServletResponse response,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		ApiCartScope scope = cartScopeFactory.fromRequest(request, user);
		writeGuestSessionHeader(response, scope, user);
		cartService.addGame(body.getGameId(), scope, resolveUserId(user));
		return ResponseEntity.ok(commerceApiMapper.toCartResponse(cartService.getCart(scope, resolveUserId(user))));
	}

	@PutMapping("/items/{itemId}")
	public ResponseEntity<CartResponse> updateItem(
			@PathVariable UUID itemId,
			@Valid @RequestBody UpdateCartItemRequest body,
			HttpServletRequest request,
			HttpServletResponse response,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		ApiCartScope scope = cartScopeFactory.fromRequest(request, user);
		writeGuestSessionHeader(response, scope, user);
		cartService.updateQuantity(itemId, body.getQuantity(), scope, resolveUserId(user));
		return ResponseEntity.ok(commerceApiMapper.toCartResponse(cartService.getCart(scope, resolveUserId(user))));
	}

	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<CartResponse> removeItem(
			@PathVariable UUID itemId,
			HttpServletRequest request,
			HttpServletResponse response,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		ApiCartScope scope = cartScopeFactory.fromRequest(request, user);
		writeGuestSessionHeader(response, scope, user);
		cartService.removeItem(itemId, scope, resolveUserId(user));
		return ResponseEntity.ok(commerceApiMapper.toCartResponse(cartService.getCart(scope, resolveUserId(user))));
	}

	@PostMapping("/promo")
	public ResponseEntity<CartResponse> applyPromo(
			@Valid @RequestBody PromoForm promoForm,
			HttpServletRequest request,
			HttpServletResponse response,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		ApiCartScope scope = cartScopeFactory.fromRequest(request, user);
		writeGuestSessionHeader(response, scope, user);
		cartService.applyPromoCode(promoForm.getCode(), scope, resolveUserId(user));
		return ResponseEntity.ok(commerceApiMapper.toCartResponse(cartService.getCart(scope, resolveUserId(user))));
	}

	private UUID resolveUserId(GameStoreUserDetails user) {
		return user != null ? user.getUser().getId() : null;
	}

	private void writeGuestSessionHeader(HttpServletResponse response, ApiCartScope scope, GameStoreUserDetails user) {
		if (user == null) {
			response.setHeader(ApiCartScopeFactory.CART_SESSION_HEADER, scope.getGuestSessionId());
		}
	}
}
