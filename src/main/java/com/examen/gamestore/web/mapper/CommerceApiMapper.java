package com.examen.gamestore.web.mapper;

import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.domain.model.OrderItem;
import com.examen.gamestore.web.dto.CartItemView;
import com.examen.gamestore.web.dto.CartView;
import com.examen.gamestore.web.dto.LibraryGameView;
import com.examen.gamestore.web.dto.OrderSummaryView;
import com.examen.gamestore.web.dto.response.CartItemResponse;
import com.examen.gamestore.web.dto.response.CartResponse;
import com.examen.gamestore.web.dto.response.LibraryGameResponse;
import com.examen.gamestore.web.dto.response.LicenseKeyResponse;
import com.examen.gamestore.web.dto.response.OrderDetailResponse;
import com.examen.gamestore.web.dto.response.OrderItemResponse;
import com.examen.gamestore.web.dto.response.OrderSummaryResponse;

@Component
public class CommerceApiMapper {

	public CartResponse toCartResponse(CartView cart) {
		return new CartResponse(
				cart.getItems().stream().map(this::toCartItemResponse).toList(),
				cart.getSubtotal(),
				cart.getDiscount(),
				cart.getTotal(),
				cart.getAppliedPromoCode(),
				cart.getItemCount());
	}

	public OrderSummaryResponse toOrderSummaryResponse(OrderSummaryView view) {
		return new OrderSummaryResponse(
				view.getId(),
				view.getOrderNumber(),
				view.getStatus().name(),
				view.getTotalAmount(),
				view.getItemCount(),
				view.getCreatedAt());
	}

	public OrderDetailResponse toOrderDetailResponse(Order order) {
		return new OrderDetailResponse(
				order.getId(),
				order.getOrderNumber(),
				order.getStatus().name(),
				order.getSubtotal(),
				order.getDiscountAmount(),
				order.getTotalAmount(),
				order.getPaymentMethod(),
				order.getCreatedAt(),
				order.getItems().stream().map(this::toOrderItemResponse).toList());
	}

	public LibraryGameResponse toLibraryResponse(LibraryGameView view) {
		return new LibraryGameResponse(
				view.getGameId(),
				view.getTitle(),
				view.getSlug(),
				view.getCoverImageUrl(),
				view.getLicenseKey(),
				view.getPurchasedAt(),
				view.getOrderNumber());
	}

	public LicenseKeyResponse toLicenseKeyResponse(LibraryGameView view) {
		return new LicenseKeyResponse(view.getGameId(), view.getLicenseKey());
	}

	private CartItemResponse toCartItemResponse(CartItemView item) {
		return new CartItemResponse(
				item.getId(),
				item.getGameId(),
				item.getTitle(),
				item.getSlug(),
				item.getCoverImageUrl(),
				item.getPlatform() != null ? item.getPlatform().name() : null,
				item.getUnitPrice(),
				item.getQuantity(),
				item.getLineTotal());
	}

	private OrderItemResponse toOrderItemResponse(OrderItem item) {
		String title = item.getGame() != null ? item.getGame().getTitle() : null;
		String slug = item.getGame() != null ? item.getGame().getSlug() : null;
		String key = item.getLicenseKey() != null ? item.getLicenseKey().getKeyValue() : null;
		return new OrderItemResponse(
				item.getId(),
				item.getGameId(),
				title,
				slug,
				item.getUnitPrice(),
				item.getQuantity(),
				key);
	}
}
