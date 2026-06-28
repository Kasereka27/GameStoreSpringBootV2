package com.examen.gamestore.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.examen.gamestore.domain.enums.OrderStatus;
import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.domain.model.OrderItem;
import com.examen.gamestore.web.dto.AdminOrderView;

public interface AdminOrderService {

	List<AdminOrderView> listOrders(OrderStatus statusFilter, int page, int pageSize);

	Optional<Order> findOrder(UUID id);

	List<OrderItem> findOrderItems(UUID orderId);

	void updateStatus(UUID orderId, OrderStatus newStatus);

	void refundOrder(UUID orderId);
}
