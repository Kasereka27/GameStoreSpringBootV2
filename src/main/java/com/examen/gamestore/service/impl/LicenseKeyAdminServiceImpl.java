package com.examen.gamestore.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.repository.GameRepository;
import com.examen.gamestore.repository.LicenseKeyRepository;
import com.examen.gamestore.service.LicenseKeyAdminService;
import com.examen.gamestore.web.dto.GameStockView;
import com.examen.gamestore.exception.GameNotFoundException;

@Service
public class LicenseKeyAdminServiceImpl implements LicenseKeyAdminService {

	private final LicenseKeyRepository licenseKeyRepository;
	private final GameRepository gameRepository;

	public LicenseKeyAdminServiceImpl(LicenseKeyRepository licenseKeyRepository, GameRepository gameRepository) {
		this.licenseKeyRepository = licenseKeyRepository;
		this.gameRepository = gameRepository;
	}

	@Override
	public List<GameStockView> getStockSummary() {
		return licenseKeyRepository.findStockSummary();
	}

	@Override
	@Transactional
	public int importKeys(UUID gameId, String keysText) {
		gameRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException(gameId.toString()));
		List<String> lines = Arrays.stream(keysText.split("\\R"))
				.map(String::trim)
				.filter(line -> !line.isEmpty())
				.collect(Collectors.toList());
		return importKeysFromCsv(gameId, lines);
	}

	@Override
	@Transactional
	public int importKeysFromCsv(UUID gameId, List<String> lines) {
		int imported = 0;
		for (String line : lines) {
			String keyValue = parseKeyLine(line);
			if (keyValue != null && !keyValue.isBlank()) {
				licenseKeyRepository.insertKey(gameId, keyValue);
				imported++;
			}
		}
		return imported;
	}

	private String parseKeyLine(String line) {
		if (line.contains(",")) {
			String[] parts = line.split(",", 2);
			return parts.length > 1 ? parts[1].trim() : parts[0].trim();
		}
		if (line.contains(";")) {
			String[] parts = line.split(";", 2);
			return parts.length > 1 ? parts[1].trim() : parts[0].trim();
		}
		return line.trim();
	}
}
