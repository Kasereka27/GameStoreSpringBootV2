package com.examen.gamestore.service;

import java.util.List;

import com.examen.gamestore.web.dto.AdminDashboardStats;
import com.examen.gamestore.web.dto.AdminOrderView;
import com.examen.gamestore.web.dto.DailySalesView;

public interface AdminDashboardService {

	AdminDashboardStats getStats();

	List<DailySalesView> getWeeklySales();

	List<AdminOrderView> getRecentOrders(int limit);

	long getPendingOrdersCount();
}
