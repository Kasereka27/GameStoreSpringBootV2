package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.web.dto.GameStockView;
import com.examen.gamestore.web.dto.LicenseKeyListView;

public interface LicenseKeyAdminService {

	List<GameStockView> getStockSummary();

	int importKeys(UUID gameId, String keysText);

	int importKeysFromCsv(UUID gameId, List<String> lines);

	List<LicenseKeyListView> listKeysByGame(UUID gameId, int page, int pageSize);

	void deleteKey(UUID keyId);
}
