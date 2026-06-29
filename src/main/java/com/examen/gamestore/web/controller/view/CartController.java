package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.exception.InsufficientStockException;
import com.examen.gamestore.exception.InvalidPromoCodeException;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.CartService;
import com.examen.gamestore.service.cart.HttpSessionCartScope;
import com.examen.gamestore.web.dto.request.PromoForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping("/panier")
	public String cart(HttpSession session, @AuthenticationPrincipal GameStoreUserDetails user, Model model) {
		populateCartModel(session, user, model);
		if (!model.containsAttribute("promoForm")) {
			model.addAttribute("promoForm", new PromoForm());
		}
		return "cart";
	}

	@PostMapping("/panier/ajouter")
	public String addToCart(
			@RequestParam UUID gameId,
			@RequestParam(required = false, defaultValue = "/panier") String redirect,
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			RedirectAttributes redirectAttributes) {

		try {
			cartService.addGame(gameId, new HttpSessionCartScope(session), resolveUserId(user));
			redirectAttributes.addFlashAttribute("cartSuccess", "Jeu ajouté au panier.");
		}
		catch (InsufficientStockException ex) {
			redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
		}
		catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
		}
		return "redirect:" + sanitizeRedirect(redirect);
	}

	@PostMapping("/panier/articles/{itemId}/quantite")
	public String updateQuantity(
			@PathVariable UUID itemId,
			@RequestParam int quantity,
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			RedirectAttributes redirectAttributes) {

		try {
			cartService.updateQuantity(itemId, quantity, new HttpSessionCartScope(session), resolveUserId(user));
		}
		catch (IllegalArgumentException | InsufficientStockException ex) {
			redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
		}
		return "redirect:/panier";
	}

	@PostMapping("/panier/articles/{itemId}/supprimer")
	public String removeItem(
			@PathVariable UUID itemId,
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			RedirectAttributes redirectAttributes) {

		try {
			cartService.removeItem(itemId, new HttpSessionCartScope(session), resolveUserId(user));
			redirectAttributes.addFlashAttribute("cartSuccess", "Article retiré du panier.");
		}
		catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("cartError", ex.getMessage());
		}
		return "redirect:/panier";
	}

	@PostMapping("/panier/promo")
	public String applyPromo(
			@Valid @ModelAttribute("promoForm") PromoForm promoForm,
			BindingResult bindingResult,
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			populateCartModel(session, user, model);
			model.addAttribute("promoError", "Saisissez un code promo valide.");
			return "cart";
		}

		try {
			cartService.applyPromoCode(promoForm.getCode(), new HttpSessionCartScope(session), resolveUserId(user));
			redirectAttributes.addFlashAttribute("promoSuccess", "Code promo appliqué.");
		}
		catch (InvalidPromoCodeException ex) {
			redirectAttributes.addFlashAttribute("promoError", ex.getMessage());
		}
		return "redirect:/panier";
	}

	@PostMapping("/panier/vider")
	public String clearCart(
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			RedirectAttributes redirectAttributes) {
		cartService.clearCart(new HttpSessionCartScope(session), resolveUserId(user));
		redirectAttributes.addFlashAttribute("cartSuccess", "Panier vidé.");
		return "redirect:/panier";
	}

	private void populateCartModel(HttpSession session, GameStoreUserDetails user, Model model) {
		model.addAttribute("cart", cartService.getCart(new HttpSessionCartScope(session), resolveUserId(user)));
		model.addAttribute("activePage", "cart");
	}

	private UUID resolveUserId(GameStoreUserDetails user) {
		return user != null ? user.getUser().getId() : null;
	}

	private String sanitizeRedirect(String redirect) {
		if (redirect == null || redirect.isBlank() || !redirect.startsWith("/") || redirect.startsWith("//")) {
			return "/panier";
		}
		return redirect;
	}
}
