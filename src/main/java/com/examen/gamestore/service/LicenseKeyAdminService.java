package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.web.dto.GameStockView;

public interface LicenseKeyAdminService {

	List<GameStockView> getStockSummary();

	int importKeys(UUID gameId, String keysText);

	int importKeysFromCsv(UUID gameId, List<String> lines);
}
