package com.examen.gamestore.web.controller.view;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.examen.gamestore.service.AdminDashboardService;

@Controller
public class AdminDashboardController {

	private final AdminDashboardService adminDashboardService;

	public AdminDashboardController(AdminDashboardService adminDashboardService) {
		this.adminDashboardService = adminDashboardService;
	}

	@GetMapping("/admin/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("dashboardStats", adminDashboardService.getStats());
		model.addAttribute("weeklySales", adminDashboardService.getWeeklySales());
		model.addAttribute("recentOrders", adminDashboardService.getRecentOrders(5));
		model.addAttribute("today", LocalDate.now());
		return "admin/dashboard";
	}
}
