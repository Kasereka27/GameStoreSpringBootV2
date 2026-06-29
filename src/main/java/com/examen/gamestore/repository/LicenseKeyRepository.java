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
import com.examen.gamestore.web.dto.GameStockView;
import com.examen.gamestore.web.dto.LicenseKeyListView;

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

	public List<GameStockView> findStockSummary() {
		return jdbcClient.sql("""
				SELECT g.id AS game_id, g.title, g.slug,
				       COUNT(*) FILTER (WHERE lk.status = 'AVAILABLE') AS available_count,
				       COUNT(*) FILTER (WHERE lk.status = 'SOLD') AS sold_count,
				       COUNT(*) AS total_count
				FROM games g
				LEFT JOIN license_keys lk ON lk.game_id = g.id
				GROUP BY g.id, g.title, g.slug
				ORDER BY available_count ASC, g.title ASC
				""")
				.query((rs, rowNum) -> {
					GameStockView view = new GameStockView();
					view.setGameId(UUID.fromString(rs.getString("game_id")));
					view.setGameTitle(rs.getString("title"));
					view.setGameSlug(rs.getString("slug"));
					view.setAvailableCount(rs.getInt("available_count"));
					view.setSoldCount(rs.getInt("sold_count"));
					view.setTotalCount(rs.getInt("total_count"));
					return view;
				})
				.list();
	}

	public int countLowStockGames(int threshold) {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*) FROM (
					SELECT game_id
					FROM license_keys
					WHERE status = 'AVAILABLE'
					GROUP BY game_id
					HAVING COUNT(*) < :threshold
				) low_stock
				""")
				.param("threshold", threshold)
				.query(Long.class)
				.single();
		return count != null ? count.intValue() : 0;
	}

	public int insertKey(UUID gameId, String keyValue) {
		return jdbcClient.sql("""
				INSERT INTO license_keys (id, game_id, key_value, status)
				VALUES (:id, :gameId, :keyValue, 'AVAILABLE')
				""")
				.param("id", UUID.randomUUID())
				.param("gameId", gameId)
				.param("keyValue", keyValue.trim())
				.update();
	}

	public void releaseKeysForOrder(UUID orderId) {
		jdbcClient.sql("""
				UPDATE license_keys
				SET status = 'AVAILABLE', order_id = NULL, assigned_at = NULL
				WHERE order_id = :orderId AND status = 'SOLD'
				""")
				.param("orderId", orderId)
				.update();
	}

	public int deleteIfAvailable(UUID id) {
		return jdbcClient.sql("""
				DELETE FROM license_keys WHERE id = :id AND status = 'AVAILABLE'
				""")
				.param("id", id)
				.update();
	}

	public List<LicenseKeyListView> findDetailedByGameId(UUID gameId, int limit, int offset) {
		return jdbcClient.sql("""
				SELECT lk.id, lk.key_value, lk.status, o.order_number
				FROM license_keys lk
				LEFT JOIN orders o ON o.id = lk.order_id
				WHERE lk.game_id = :gameId
				ORDER BY lk.status ASC, lk.id DESC
				LIMIT :limit OFFSET :offset
				""")
				.param("gameId", gameId)
				.param("limit", limit)
				.param("offset", offset)
				.query((rs, rowNum) -> {
					LicenseKeyListView view = new LicenseKeyListView();
					view.setId(UUID.fromString(rs.getString("id")));
					view.setKeyValue(rs.getString("key_value"));
					view.setStatus(LicenseKeyStatus.fromString(rs.getString("status")));
					view.setOrderNumber(rs.getString("order_number"));
					return view;
				})
				.list();
	}

	public List<LicenseKey> findByGameId(UUID gameId, int limit) {
		return jdbcClient.sql("""
				SELECT id, game_id, key_value, status, order_id, assigned_at
				FROM license_keys
				WHERE game_id = :gameId
				ORDER BY status ASC, id DESC
				LIMIT :limit
				""")
				.param("gameId", gameId)
				.param("limit", limit)
				.query(this::mapRow)
				.list();
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
