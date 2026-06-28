package com.examen.gamestore.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.enums.OrderStatus;
import com.examen.gamestore.domain.model.LicenseKey;
import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.domain.model.OrderItem;
import com.examen.gamestore.domain.model.PromoCode;
import com.examen.gamestore.exception.EmptyCartException;
import com.examen.gamestore.exception.InsufficientStockException;
import com.examen.gamestore.exception.OrderNotFoundException;
import com.examen.gamestore.infrastructure.email.EmailService;
import com.examen.gamestore.repository.LicenseKeyRepository;
import com.examen.gamestore.repository.OrderRepository;
import com.examen.gamestore.repository.PromoCodeRepository;
import com.examen.gamestore.service.CartService;
import com.examen.gamestore.service.OrderService;
import com.examen.gamestore.web.dto.CartItemView;
import com.examen.gamestore.web.dto.CartView;
import com.examen.gamestore.web.dto.LibraryGameView;
import com.examen.gamestore.web.dto.OrderSummaryView;
import com.examen.gamestore.web.dto.request.CheckoutForm;

import jakarta.servlet.http.HttpSession;

@Service
public class OrderServiceImpl implements OrderService {

	private final CartService cartService;
	private final OrderRepository orderRepository;
	private final LicenseKeyRepository licenseKeyRepository;
	private final PromoCodeRepository promoCodeRepository;
	private final EmailService emailService;

	public OrderServiceImpl(
			CartService cartService,
			OrderRepository orderRepository,
			LicenseKeyRepository licenseKeyRepository,
			PromoCodeRepository promoCodeRepository,
			EmailService emailService) {
		this.cartService = cartService;
		this.orderRepository = orderRepository;
		this.licenseKeyRepository = licenseKeyRepository;
		this.promoCodeRepository = promoCodeRepository;
		this.emailService = emailService;
	}

	@Override
	@Transactional
	public Order checkout(UUID userId, CheckoutForm form, HttpSession session) {
		CartView cart = cartService.getCart(session, userId);
		if (cart.isEmpty()) {
			throw new EmptyCartException();
		}

		for (CartItemView item : cart.getItems()) {
			if (licenseKeyRepository.countAvailableByGameId(item.getGameId()) < item.getQuantity()) {
				throw new InsufficientStockException(item.getTitle());
			}
		}

		PromoCode promo = cartService.resolvePromo(session);
		UUID promoId = promo != null ? promo.getId() : null;

		Order order = new Order();
		order.setId(UUID.randomUUID());
		order.setOrderNumber(generateOrderNumber());
		order.setUserId(userId);
		order.setStatus(OrderStatus.PENDING);
		order.setSubtotal(cart.getSubtotal());
		order.setDiscountAmount(cart.getDiscount());
		order.setTotalAmount(cart.getTotal());
		order.setPaymentMethod(form.getPaymentMethod());
		order.setPromoCodeId(promoId);
		order.setBillingFirstName(form.getFirstName());
		order.setBillingLastName(form.getLastName());
		order.setBillingEmail(form.getEmail());
		order.setBillingPhone(form.getPhone());
		order.setBillingAddress(form.getAddress());
		order.setBillingPostalCode(form.getPostalCode());
		order.setBillingCity(form.getCity());
		order.setBillingCountry(form.getCountry());

		orderRepository.insert(order);

		List<OrderItem> createdItems = new ArrayList<>();
		for (CartItemView cartItem : cart.getItems()) {
			List<LicenseKey> keys = licenseKeyRepository.findAvailableByGameId(
					cartItem.getGameId(), cartItem.getQuantity());
			if (keys.size() < cartItem.getQuantity()) {
				throw new InsufficientStockException(cartItem.getTitle());
			}
			for (LicenseKey key : keys) {
				OrderItem orderItem = new OrderItem();
				orderItem.setId(UUID.randomUUID());
				orderItem.setOrderId(order.getId());
				orderItem.setGameId(cartItem.getGameId());
				orderItem.setLicenseKeyId(key.getId());
				orderItem.setUnitPrice(cartItem.getUnitPrice());
				orderItem.setQuantity(1);
				orderRepository.insertItem(orderItem);
				licenseKeyRepository.assignToOrder(key.getId(), order.getId());
				createdItems.add(orderItem);
			}
		}

		orderRepository.updateStatus(order.getId(), OrderStatus.COMPLETED);
		order.setStatus(OrderStatus.COMPLETED);
		order.setItems(createdItems);

		if (promo != null) {
			promoCodeRepository.incrementUsage(promo.getId());
		}

		cartService.clearCart(session, userId);

		emailService.sendOrderConfirmation(
				form.getEmail(),
				form.getFirstName(),
				order.getOrderNumber(),
				order.getTotalAmount());

		return orderRepository.findById(order.getId()).orElse(order);
	}

	@Override
	public List<OrderSummaryView> getOrderSummaries(UUID userId) {
		return orderRepository.findSummariesByUserId(userId);
	}

	@Override
	public Order getOrderForUser(UUID orderId, UUID userId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
		if (!order.getUserId().equals(userId)) {
			throw new OrderNotFoundException(orderId.toString());
		}
		order.setItems(orderRepository.findItemsByOrderId(orderId));
		return order;
	}

	@Override
	public List<LibraryGameView> getLibrary(UUID userId) {
		return orderRepository.findLibraryByUserId(userId);
	}

	@Override
	public Order getOrderByNumberForUser(String orderNumber, UUID userId) {
		Order order = orderRepository.findByOrderNumber(orderNumber)
				.orElseThrow(() -> new OrderNotFoundException(orderNumber));
		if (!order.getUserId().equals(userId)) {
			throw new OrderNotFoundException(orderNumber);
		}
		order.setItems(orderRepository.findItemsByOrderId(order.getId()));
		return order;
	}

	private String generateOrderNumber() {
		String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int random = ThreadLocalRandom.current().nextInt(1000, 9999);
		return "GS-" + datePart + "-" + random;
	}
}
