package com.examen.gamestore.web.dto.response;

import java.util.UUID;

public record LicenseKeyResponse(
		UUID gameId,
		String licenseKey
) {
}
