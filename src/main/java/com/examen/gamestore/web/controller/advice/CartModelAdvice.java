package com.examen.gamestore.web.controller.advice;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.CartService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CartModelAdvice {

	private final CartService cartService;

	public CartModelAdvice(CartService cartService) {
		this.cartService = cartService;
	}

	@ModelAttribute("cartItemCount")
	public int cartItemCount(HttpSession session, @AuthenticationPrincipal GameStoreUserDetails user) {
		return cartService.getItemCount(session, resolveUserId(user));
	}

	private UUID resolveUserId(GameStoreUserDetails user) {
		return user != null ? user.getUser().getId() : null;
	}
}
