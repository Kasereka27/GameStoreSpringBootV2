package com.examen.gamestore.service.cart;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ApiCartMetadataStore {

	private final Map<String, String> promoCodes = new ConcurrentHashMap<>();
	private final Map<String, Boolean> mergedFlags = new ConcurrentHashMap<>();

	public String getPromoCode(String storageKey) {
		return promoCodes.get(storageKey);
	}

	public void setPromoCode(String storageKey, String code) {
		if (code == null || code.isBlank()) {
			promoCodes.remove(storageKey);
		}
		else {
			promoCodes.put(storageKey, code);
		}
	}

	public void clearPromoCode(String storageKey) {
		promoCodes.remove(storageKey);
	}

	public boolean isMerged(String storageKey) {
		return Boolean.TRUE.equals(mergedFlags.get(storageKey));
	}

	public void markMerged(String storageKey) {
		mergedFlags.put(storageKey, true);
	}

	public static String storageKey(UUID userId, String guestSessionId) {
		if (userId != null) {
			return "user:" + userId;
		}
		return "guest:" + guestSessionId;
	}
}
