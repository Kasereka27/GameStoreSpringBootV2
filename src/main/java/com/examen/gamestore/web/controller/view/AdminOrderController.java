package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.domain.enums.OrderStatus;
import com.examen.gamestore.exception.OrderNotFoundException;
import com.examen.gamestore.service.AdminOrderService;

@Controller
public class AdminOrderController {

	private final AdminOrderService adminOrderService;

	public AdminOrderController(AdminOrderService adminOrderService) {
		this.adminOrderService = adminOrderService;
	}

	@GetMapping("/admin/orders")
	public String listOrders(
			@RequestParam(required = false) OrderStatus status,
			@RequestParam(defaultValue = "1") int page,
			Model model) {
		model.addAttribute("orders", adminOrderService.listOrders(status, page, 20));
		model.addAttribute("statusFilter", status);
		model.addAttribute("page", page);
		model.addAttribute("orderStatuses", OrderStatus.values());
		return "admin/orders";
	}

	@GetMapping("/admin/orders/{id}")
	public String orderDetail(@PathVariable UUID id, Model model) {
		var order = adminOrderService.findOrder(id)
				.orElseThrow(() -> new OrderNotFoundException(id.toString()));
		model.addAttribute("order", order);
		model.addAttribute("items", adminOrderService.findOrderItems(id));
		model.addAttribute("orderStatuses", OrderStatus.values());
		return "admin/order-detail";
	}

	@PostMapping("/admin/orders/{id}/status")
	public String updateStatus(
			@PathVariable UUID id,
			@RequestParam OrderStatus status,
			RedirectAttributes redirectAttributes) {
		try {
			adminOrderService.updateStatus(id, status);
			redirectAttributes.addFlashAttribute("successMessage", "Statut mis à jour.");
		}
		catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/orders/" + id;
	}

	@PostMapping("/admin/orders/{id}/refund")
	public String refund(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			adminOrderService.refundOrder(id);
			redirectAttributes.addFlashAttribute("successMessage", "Commande remboursée — clés libérées.");
		}
		catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/orders/" + id;
	}
}
