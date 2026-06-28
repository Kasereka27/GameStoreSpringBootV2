package com.examen.gamestore.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/a-propos")
	public String aPropos() {
		return "a-propos";
	}

	@GetMapping("/panier")
	public String cart() {
		return "cart";
	}

	@GetMapping("/checkout")
	public String checkout() {
		return "checkout";
	}

	@GetMapping("/maintenance")
	public String maintenance() {
		return "maintenance";
	}

	@GetMapping("/403")
	public String forbidden() {
		return "403";
	}

	@GetMapping("/admin")
	public String adminIndex() {
		return "admin/index";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard() {
		return "admin/dashboard";
	}
}
