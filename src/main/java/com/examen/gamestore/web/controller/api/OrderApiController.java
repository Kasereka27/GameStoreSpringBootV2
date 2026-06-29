package com.examen.gamestore.web.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.OrderService;
import com.examen.gamestore.service.cart.ApiCartScope;
import com.examen.gamestore.service.cart.ApiCartScopeFactory;
import com.examen.gamestore.web.dto.request.CheckoutForm;
import com.examen.gamestore.web.dto.response.OrderDetailResponse;
import com.examen.gamestore.web.dto.response.OrderSummaryResponse;
import com.examen.gamestore.web.mapper.CommerceApiMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

	private final OrderService orderService;
	private final ApiCartScopeFactory cartScopeFactory;
	private final CommerceApiMapper commerceApiMapper;

	public OrderApiController(
			OrderService orderService,
			ApiCartScopeFactory cartScopeFactory,
			CommerceApiMapper commerceApiMapper) {
		this.orderService = orderService;
		this.cartScopeFactory = cartScopeFactory;
		this.commerceApiMapper = commerceApiMapper;
	}

	@PostMapping("/checkout")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderDetailResponse checkout(
			@Valid @RequestBody CheckoutForm form,
			HttpServletRequest request,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		ApiCartScope scope = cartScopeFactory.fromRequest(request, user);
		var order = orderService.checkout(user.getUser().getId(), form, scope);
		return commerceApiMapper.toOrderDetailResponse(order);
	}

	@GetMapping
	public List<OrderSummaryResponse> listOrders(@AuthenticationPrincipal GameStoreUserDetails user) {
		return orderService.getOrderSummaries(user.getUser().getId()).stream()
				.map(commerceApiMapper::toOrderSummaryResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public OrderDetailResponse orderDetail(
			@PathVariable UUID id,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		return commerceApiMapper.toOrderDetailResponse(
				orderService.getOrderForUser(id, user.getUser().getId()));
	}
}
