package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.PromoCode;
import com.examen.gamestore.web.dto.request.PromoCodeForm;

public interface AdminPromoService {

	List<PromoCode> findAll();

	UUID create(PromoCodeForm form);

	void setActive(UUID id, boolean active);
}
