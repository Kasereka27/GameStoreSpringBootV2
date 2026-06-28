package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.enums.DiscountType;
import com.examen.gamestore.domain.model.PromoCode;

@Repository
public class PromoCodeRepository {

	private final JdbcClient jdbcClient;

	public PromoCodeRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public Optional<PromoCode> findByCode(String code) {
		return jdbcClient.sql("""
				SELECT id, code, discount_type, discount_value, min_order_amount,
				       max_usages, usage_count, expires_at, active
				FROM promo_codes WHERE UPPER(code) = UPPER(:code)
				""")
				.param("code", code)
				.query(this::mapRow)
				.optional();
	}

	public void incrementUsage(UUID id) {
		jdbcClient.sql("""
				UPDATE promo_codes SET usage_count = usage_count + 1 WHERE id = :id
				""")
				.param("id", id)
				.update();
	}

	private PromoCode mapRow(ResultSet rs, int rowNum) throws SQLException {
		PromoCode promo = new PromoCode();
		promo.setId(UUID.fromString(rs.getString("id")));
		promo.setCode(rs.getString("code"));
		promo.setDiscountType(DiscountType.fromString(rs.getString("discount_type")));
		promo.setDiscountValue(rs.getBigDecimal("discount_value"));
		promo.setMinOrderAmount(rs.getBigDecimal("min_order_amount"));
		int maxUsages = rs.getInt("max_usages");
		if (!rs.wasNull()) {
			promo.setMaxUsages(maxUsages);
		}
		promo.setUsageCount(rs.getInt("usage_count"));
		promo.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
		promo.setActive(rs.getBoolean("active"));
		return promo;
	}
}
