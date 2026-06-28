package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.enums.OrderStatus;
import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.domain.model.OrderItem;
import com.examen.gamestore.repository.LicenseKeyRepository;
import com.examen.gamestore.repository.OrderRepository;
import com.examen.gamestore.service.AdminOrderService;
import com.examen.gamestore.web.dto.AdminOrderView;
import com.examen.gamestore.exception.GameNotFoundException;
import com.examen.gamestore.exception.OrderNotFoundException;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

	private static final int DEFAULT_PAGE_SIZE = 20;

	private final OrderRepository orderRepository;
	private final LicenseKeyRepository licenseKeyRepository;

	public AdminOrderServiceImpl(OrderRepository orderRepository, LicenseKeyRepository licenseKeyRepository) {
		this.orderRepository = orderRepository;
		this.licenseKeyRepository = licenseKeyRepository;
	}

	@Override
	public List<AdminOrderView> listOrders(OrderStatus statusFilter, int page, int pageSize) {
		int size = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
		int safePage = Math.max(page, 1);
		int offset = (safePage - 1) * size;
		return orderRepository.findAllForAdmin(statusFilter, size, offset);
	}

	@Override
	public Optional<Order> findOrder(UUID id) {
		return orderRepository.findById(id);
	}

	@Override
	public List<OrderItem> findOrderItems(UUID orderId) {
		return orderRepository.findItemsByOrderId(orderId);
	}

	@Override
	@Transactional
	public void updateStatus(UUID orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
		if (newStatus == OrderStatus.REFUNDED) {
			refundOrder(orderId);
			return;
		}
		if (newStatus == OrderStatus.CANCELLED && order.getStatus() == OrderStatus.PENDING) {
			licenseKeyRepository.releaseKeysForOrder(orderId);
		}
		orderRepository.updateStatus(orderId, newStatus);
	}

	@Override
	@Transactional
	public void refundOrder(UUID orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
		if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.PAID) {
			throw new IllegalStateException("Seules les commandes payées peuvent être remboursées");
		}
		licenseKeyRepository.releaseKeysForOrder(orderId);
		orderRepository.updateStatus(orderId, OrderStatus.REFUNDED);
	}
}
