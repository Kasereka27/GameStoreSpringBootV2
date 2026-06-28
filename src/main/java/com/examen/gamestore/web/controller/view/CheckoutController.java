package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.exception.EmptyCartException;
import com.examen.gamestore.exception.InsufficientStockException;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.CartService;
import com.examen.gamestore.service.OrderService;
import com.examen.gamestore.web.dto.request.CheckoutForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CheckoutController {

	private final CartService cartService;
	private final OrderService orderService;

	public CheckoutController(CartService cartService, OrderService orderService) {
		this.cartService = cartService;
		this.orderService = orderService;
	}

	@GetMapping("/checkout")
	public String checkout(
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			Model model) {

		var cart = cartService.getCart(session, user.getUser().getId());
		if (cart.isEmpty()) {
			return "redirect:/panier";
		}

		model.addAttribute("cart", cart);
		model.addAttribute("activePage", "checkout");

		if (!model.containsAttribute("checkoutForm")) {
			CheckoutForm form = new CheckoutForm();
			var profile = user.getUser();
			form.setFirstName(profile.getFirstName());
			form.setLastName(profile.getLastName());
			form.setEmail(profile.getEmail());
			model.addAttribute("checkoutForm", form);
		}

		return "checkout";
	}

	@PostMapping("/checkout/payer")
	public String pay(
			@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
			BindingResult bindingResult,
			HttpSession session,
			@AuthenticationPrincipal GameStoreUserDetails user,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("cart", cartService.getCart(session, user.getUser().getId()));
			model.addAttribute("activePage", "checkout");
			return "checkout";
		}

		try {
			var order = orderService.checkout(user.getUser().getId(), checkoutForm, session);
			redirectAttributes.addFlashAttribute("orderNumber", order.getOrderNumber());
			return "redirect:/checkout/confirmation?order=" + order.getOrderNumber();
		}
		catch (EmptyCartException | InsufficientStockException ex) {
			redirectAttributes.addFlashAttribute("checkoutError", ex.getMessage());
			return "redirect:/panier";
		}
	}

	@GetMapping("/checkout/confirmation")
	public String confirmation(
			@RequestParam String order,
			@AuthenticationPrincipal GameStoreUserDetails user,
			Model model) {

		model.addAttribute("order", orderService.getOrderByNumberForUser(order, user.getUser().getId()));
		model.addAttribute("activePage", "checkout");
		return "checkout-confirmation";
	}
}
