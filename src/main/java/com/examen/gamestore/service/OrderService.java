package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.service.cart.CartScope;
import com.examen.gamestore.web.dto.LibraryGameView;
import com.examen.gamestore.web.dto.OrderSummaryView;
import com.examen.gamestore.web.dto.request.CheckoutForm;

public interface OrderService {

	Order checkout(UUID userId, CheckoutForm form, CartScope cartScope);

	List<OrderSummaryView> getOrderSummaries(UUID userId);

	Order getOrderForUser(UUID orderId, UUID userId);

	List<LibraryGameView> getLibrary(UUID userId);

	Order getOrderByNumberForUser(String orderNumber, UUID userId);
}
