package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.PromoCode;
import com.examen.gamestore.repository.PromoCodeRepository;
import com.examen.gamestore.service.AdminPromoService;
import com.examen.gamestore.web.dto.request.PromoCodeForm;

@Service
public class AdminPromoServiceImpl implements AdminPromoService {

	private final PromoCodeRepository promoCodeRepository;

	public AdminPromoServiceImpl(PromoCodeRepository promoCodeRepository) {
		this.promoCodeRepository = promoCodeRepository;
	}

	@Override
	public List<PromoCode> findAll() {
		return promoCodeRepository.findAll();
	}

	@Override
	@Transactional
	public UUID create(PromoCodeForm form) {
		if (promoCodeRepository.existsByCode(form.getCode())) {
			throw new IllegalArgumentException("Ce code promo existe déjà");
		}
		PromoCode promo = new PromoCode();
		promo.setCode(form.getCode());
		promo.setDiscountType(form.getDiscountType());
		promo.setDiscountValue(form.getDiscountValue());
		promo.setMinOrderAmount(form.getMinOrderAmount());
		promo.setMaxUsages(form.getMaxUsages());
		promo.setExpiresAt(form.getExpiresAt());
		promo.setActive(form.isActive());
		return promoCodeRepository.insert(promo);
	}

	@Override
	@Transactional
	public void setActive(UUID id, boolean active) {
		promoCodeRepository.updateActive(id, active);
	}
}
