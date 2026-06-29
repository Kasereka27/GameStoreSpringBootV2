package com.examen.gamestore.service.cart;

import java.util.UUID;

public class ApiCartScope implements CartScope {

	private final String guestSessionId;
	private final boolean newGuestSession;
	private final UUID userId;
	private final ApiCartMetadataStore metadataStore;

	public ApiCartScope(String guestSessionId, boolean newGuestSession, UUID userId, ApiCartMetadataStore metadataStore) {
		this.guestSessionId = guestSessionId;
		this.newGuestSession = newGuestSession;
		this.userId = userId;
		this.metadataStore = metadataStore;
	}

	public boolean isNewGuestSession() {
		return newGuestSession;
	}

	public String getGuestSessionId() {
		return guestSessionId;
	}

	@Override
	public String getOrCreateGuestSessionId() {
		return guestSessionId;
	}

	@Override
	public String getPromoCode() {
		return metadataStore.getPromoCode(storageKey());
	}

	@Override
	public void setPromoCode(String code) {
		metadataStore.setPromoCode(storageKey(), code);
	}

	@Override
	public void clearPromoCode() {
		metadataStore.clearPromoCode(storageKey());
	}

	@Override
	public boolean isGuestCartMerged() {
		if (userId == null) {
			return false;
		}
		return metadataStore.isMerged(ApiCartMetadataStore.storageKey(userId, null));
	}

	@Override
	public void markGuestCartMerged() {
		if (userId != null) {
			metadataStore.markMerged(ApiCartMetadataStore.storageKey(userId, null));
		}
	}

	private String storageKey() {
		return ApiCartMetadataStore.storageKey(userId, guestSessionId);
	}
}
