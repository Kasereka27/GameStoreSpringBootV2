package com.examen.gamestore.web.controller.api;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.exception.GameNotFoundException;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.OrderService;
import com.examen.gamestore.web.dto.response.LicenseKeyResponse;
import com.examen.gamestore.web.dto.response.LibraryGameResponse;
import com.examen.gamestore.web.mapper.CommerceApiMapper;

@RestController
@RequestMapping("/api/library")
public class LibraryApiController {

	private final OrderService orderService;
	private final CommerceApiMapper commerceApiMapper;

	public LibraryApiController(OrderService orderService, CommerceApiMapper commerceApiMapper) {
		this.orderService = orderService;
		this.commerceApiMapper = commerceApiMapper;
	}

	@GetMapping
	public List<LibraryGameResponse> library(@AuthenticationPrincipal GameStoreUserDetails user) {
		return orderService.getLibrary(user.getUser().getId()).stream()
				.map(commerceApiMapper::toLibraryResponse)
				.toList();
	}

	@GetMapping("/{gameId}/key")
	public LicenseKeyResponse gameLicenseKey(
			@PathVariable UUID gameId,
			@AuthenticationPrincipal GameStoreUserDetails user) {
		var entry = orderService.getLibrary(user.getUser().getId()).stream()
				.filter(game -> game.getGameId().equals(gameId))
				.findFirst()
				.orElseThrow(() -> new GameNotFoundException(gameId.toString()));
		return commerceApiMapper.toLicenseKeyResponse(entry);
	}
}
