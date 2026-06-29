package com.examen.gamestore.service.cart;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ApiCartScopeFactory {

	public static final String CART_SESSION_HEADER = "X-Cart-Session";

	private final ApiCartMetadataStore metadataStore;

	public ApiCartScopeFactory(ApiCartMetadataStore metadataStore) {
		this.metadataStore = metadataStore;
	}

	public ApiCartScope fromRequest(HttpServletRequest request, GameStoreUserDetails user) {
		UUID userId = user != null ? user.getUser().getId() : null;
		String header = request.getHeader(CART_SESSION_HEADER);
		boolean newSession = header == null || header.isBlank();
		String guestSessionId = newSession ? UUID.randomUUID().toString() : header.trim();
		return new ApiCartScope(guestSessionId, newSession, userId, metadataStore);
	}
}
