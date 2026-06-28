package com.examen.gamestore.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.examen.gamestore.domain.enums.OrderStatus;
import com.examen.gamestore.repository.LicenseKeyRepository;
import com.examen.gamestore.repository.OrderRepository;
import com.examen.gamestore.repository.UserRepository;
import com.examen.gamestore.service.AdminDashboardService;
import com.examen.gamestore.web.dto.AdminDashboardStats;
import com.examen.gamestore.web.dto.AdminOrderView;
import com.examen.gamestore.web.dto.DailySalesView;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private static final int LOW_STOCK_THRESHOLD = 10;
	private static final int WEEK_DAYS = 7;

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final LicenseKeyRepository licenseKeyRepository;

	public AdminDashboardServiceImpl(
			OrderRepository orderRepository,
			UserRepository userRepository,
			LicenseKeyRepository licenseKeyRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.licenseKeyRepository = licenseKeyRepository;
	}

	@Override
	public AdminDashboardStats getStats() {
		AdminDashboardStats stats = new AdminDashboardStats();
		stats.setDailyRevenue(orderRepository.sumRevenueToday());
		stats.setDailyOrders(orderRepository.countOrdersToday());
		stats.setNewUsersToday(userRepository.countCreatedToday());
		stats.setLowStockGames(licenseKeyRepository.countLowStockGames(LOW_STOCK_THRESHOLD));
		stats.setPendingOrdersCount(orderRepository.countByStatus(OrderStatus.PENDING));
		return stats;
	}

	@Override
	public List<DailySalesView> getWeeklySales() {
		LocalDate today = LocalDate.now();
		LocalDate start = today.minusDays(WEEK_DAYS - 1L);
		Map<LocalDate, DailySalesView> byDate = orderRepository
				.findDailySalesLastDays(start)
				.stream()
				.collect(Collectors.toMap(DailySalesView::getDate, v -> v));

		List<DailySalesView> week = new ArrayList<>();

		BigDecimal maxRevenue = BigDecimal.ZERO;
		for (int i = 0; i < WEEK_DAYS; i++) {
			LocalDate date = start.plusDays(i);
			DailySalesView day = byDate.getOrDefault(date, emptyDay(date));
			week.add(day);
			if (day.getRevenue().compareTo(maxRevenue) > 0) {
				maxRevenue = day.getRevenue();
			}
		}

		for (DailySalesView day : week) {
			day.setDayLabel(dayLabel(day.getDate()));
			if (maxRevenue.compareTo(BigDecimal.ZERO) > 0) {
				int percent = day.getRevenue()
						.multiply(BigDecimal.valueOf(100))
						.divide(maxRevenue, 0, java.math.RoundingMode.HALF_UP)
						.intValue();
				day.setBarHeightPercent(Math.max(percent, 5));
			}
			else {
				day.setBarHeightPercent(5);
			}
		}
		return week;
	}

	@Override
	public List<AdminOrderView> getRecentOrders(int limit) {
		return orderRepository.findRecentForAdmin(limit);
	}

	@Override
	public long getPendingOrdersCount() {
		return orderRepository.countByStatus(OrderStatus.PENDING);
	}

	private DailySalesView emptyDay(LocalDate date) {
		DailySalesView view = new DailySalesView();
		view.setDate(date);
		view.setRevenue(BigDecimal.ZERO);
		view.setOrderCount(0);
		return view;
	}

	private String dayLabel(LocalDate date) {
		DayOfWeek dow = date.getDayOfWeek();
		return dow.getDisplayName(TextStyle.SHORT, Locale.FRENCH);
	}
}
