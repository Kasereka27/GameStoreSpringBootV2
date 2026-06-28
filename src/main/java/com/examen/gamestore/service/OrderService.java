package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.web.dto.LibraryGameView;
import com.examen.gamestore.web.dto.OrderSummaryView;
import com.examen.gamestore.web.dto.request.CheckoutForm;

import jakarta.servlet.http.HttpSession;

public interface OrderService {

	Order checkout(UUID userId, CheckoutForm form, HttpSession session);

	List<OrderSummaryView> getOrderSummaries(UUID userId);

	Order getOrderForUser(UUID orderId, UUID userId);

	List<LibraryGameView> getLibrary(UUID userId);

	Order getOrderByNumberForUser(String orderNumber, UUID userId);
}
