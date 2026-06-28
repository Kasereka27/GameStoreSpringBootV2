package com.examen.gamestore.web.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.examen.gamestore.service.AdminDashboardService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AdminSidebarAdvice {

	private final AdminDashboardService adminDashboardService;

	public AdminSidebarAdvice(AdminDashboardService adminDashboardService) {
		this.adminDashboardService = adminDashboardService;
	}

	@ModelAttribute("pendingOrdersCount")
	public long pendingOrdersCount(HttpServletRequest request) {
		String uri = request.getRequestURI();
		if (uri != null && uri.startsWith("/admin")) {
			return adminDashboardService.getPendingOrdersCount();
		}
		return 0L;
	}
}
