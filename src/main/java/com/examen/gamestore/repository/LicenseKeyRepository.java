package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.enums.LicenseKeyStatus;
import com.examen.gamestore.domain.model.LicenseKey;

@Repository
public class LicenseKeyRepository {

	private final JdbcClient jdbcClient;

	public LicenseKeyRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<LicenseKey> findAvailableByGameId(UUID gameId, int limit) {
		return jdbcClient.sql("""
				SELECT id, game_id, key_value, status, order_id, assigned_at
				FROM license_keys
				WHERE game_id = :gameId AND status = 'AVAILABLE'
				ORDER BY id ASC
				LIMIT :limit
				""")
				.param("gameId", gameId)
				.param("limit", limit)
				.query(this::mapRow)
				.list();
	}

	public int countAvailableByGameId(UUID gameId) {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*) FROM license_keys
				WHERE game_id = :gameId AND status = 'AVAILABLE'
				""")
				.param("gameId", gameId)
				.query(Long.class)
				.single();
		return count != null ? count.intValue() : 0;
	}

	public void assignToOrder(UUID keyId, UUID orderId) {
		jdbcClient.sql("""
				UPDATE license_keys
				SET status = 'SOLD', order_id = :orderId, assigned_at = CURRENT_TIMESTAMP
				WHERE id = :id AND status = 'AVAILABLE'
				""")
				.param("id", keyId)
				.param("orderId", orderId)
				.update();
	}

	public Optional<LicenseKey> findById(UUID id) {
		return jdbcClient.sql("""
				SELECT id, game_id, key_value, status, order_id, assigned_at
				FROM license_keys WHERE id = :id
				""")
				.param("id", id)
				.query(this::mapRow)
				.optional();
	}

	public List<LicenseKey> findByUserId(UUID userId) {
		return jdbcClient.sql("""
				SELECT lk.id, lk.game_id, lk.key_value, lk.status, lk.order_id, lk.assigned_at
				FROM license_keys lk
				INNER JOIN orders o ON o.id = lk.order_id
				WHERE o.user_id = :userId AND lk.status = 'SOLD'
				ORDER BY lk.assigned_at DESC
				""")
				.param("userId", userId)
				.query(this::mapRow)
				.list();
	}

	private LicenseKey mapRow(ResultSet rs, int rowNum) throws SQLException {
		LicenseKey key = new LicenseKey();
		key.setId(UUID.fromString(rs.getString("id")));
		key.setGameId(UUID.fromString(rs.getString("game_id")));
		key.setKeyValue(rs.getString("key_value"));
		key.setStatus(LicenseKeyStatus.fromString(rs.getString("status")));
		String orderId = rs.getString("order_id");
		if (orderId != null) {
			key.setOrderId(UUID.fromString(orderId));
		}
		key.setAssignedAt(rs.getObject("assigned_at", LocalDateTime.class));
		return key;
	}
}
