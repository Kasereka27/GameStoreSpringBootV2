package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.domain.enums.DiscountType;
import com.examen.gamestore.service.AdminPromoService;
import com.examen.gamestore.web.dto.request.PromoCodeForm;

import jakarta.validation.Valid;

@Controller
public class AdminPromoController {

	private final AdminPromoService adminPromoService;

	public AdminPromoController(AdminPromoService adminPromoService) {
		this.adminPromoService = adminPromoService;
	}

	@GetMapping("/admin/promos")
	public String listPromos(Model model) {
		model.addAttribute("promos", adminPromoService.findAll());
		if (!model.containsAttribute("promoForm")) {
			model.addAttribute("promoForm", new PromoCodeForm());
		}
		model.addAttribute("discountTypes", DiscountType.values());
		return "admin/promos";
	}

	@PostMapping("/admin/promos")
	public String createPromo(
			@Valid @ModelAttribute("promoForm") PromoCodeForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("promos", adminPromoService.findAll());
			model.addAttribute("discountTypes", DiscountType.values());
			return "admin/promos";
		}

		try {
			adminPromoService.create(form);
			redirectAttributes.addFlashAttribute("successMessage", "Code promo créé.");
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("code", "duplicate", ex.getMessage());
			model.addAttribute("promos", adminPromoService.findAll());
			model.addAttribute("discountTypes", DiscountType.values());
			return "admin/promos";
		}
		return "redirect:/admin/promos";
	}

	@PostMapping("/admin/promos/{id}/toggle")
	public String toggleActive(
			@PathVariable UUID id,
			@RequestParam boolean active,
			RedirectAttributes redirectAttributes) {
		adminPromoService.setActive(id, active);
		redirectAttributes.addFlashAttribute("successMessage",
				active ? "Code promo activé." : "Code promo désactivé.");
		return "redirect:/admin/promos";
	}
}
