package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.domain.model.CartItem;
import com.examen.gamestore.domain.model.Game;

@Repository
public class CartRepository {

	private final JdbcClient jdbcClient;

	public CartRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<CartItem> findByUserId(UUID userId) {
		return jdbcClient.sql("""
				SELECT ci.id, ci.user_id, ci.session_id, ci.game_id, ci.quantity, ci.created_at,
				       g.id AS g_id, g.title, g.slug, g.cover_image_url, g.platform,
				       g.base_price, g.discounted_price, g.discount_end_date, g.status
				FROM cart_items ci
				INNER JOIN games g ON g.id = ci.game_id
				WHERE ci.user_id = :userId
				ORDER BY ci.created_at ASC
				""")
				.param("userId", userId)
				.query(this::mapCartItemRow)
				.list();
	}

	public List<CartItem> findBySessionId(String sessionId) {
		return jdbcClient.sql("""
				SELECT ci.id, ci.user_id, ci.session_id, ci.game_id, ci.quantity, ci.created_at,
				       g.id AS g_id, g.title, g.slug, g.cover_image_url, g.platform,
				       g.base_price, g.discounted_price, g.discount_end_date, g.status
				FROM cart_items ci
				INNER JOIN games g ON g.id = ci.game_id
				WHERE ci.session_id = :sessionId
				ORDER BY ci.created_at ASC
				""")
				.param("sessionId", sessionId)
				.query(this::mapCartItemRow)
				.list();
	}

	public Optional<CartItem> findById(UUID id) {
		return jdbcClient.sql("""
				SELECT ci.id, ci.user_id, ci.session_id, ci.game_id, ci.quantity, ci.created_at,
				       g.id AS g_id, g.title, g.slug, g.cover_image_url, g.platform,
				       g.base_price, g.discounted_price, g.discount_end_date, g.status
				FROM cart_items ci
				INNER JOIN games g ON g.id = ci.game_id
				WHERE ci.id = :id
				""")
				.param("id", id)
				.query(this::mapCartItemRow)
				.optional();
	}

	public int countByUserId(UUID userId) {
		return countItems("user_id = :owner", java.util.Map.of("owner", userId));
	}

	public int countBySessionId(String sessionId) {
		return countItems("session_id = :owner", java.util.Map.of("owner", sessionId));
	}

	private int countItems(String whereClause, java.util.Map<String, Object> params) {
		Long count = jdbcClient.sql("SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE " + whereClause)
				.params(params)
				.query(Long.class)
				.single();
		return count != null ? count.intValue() : 0;
	}

	public UUID insert(UUID userId, String sessionId, UUID gameId, int quantity) {
		UUID id = UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO cart_items (id, user_id, session_id, game_id, quantity, created_at)
				VALUES (:id, :userId, :sessionId, :gameId, :quantity, CURRENT_TIMESTAMP)
				""")
				.param("id", id)
				.param("userId", userId)
				.param("sessionId", sessionId)
				.param("gameId", gameId)
				.param("quantity", quantity)
				.update();
		return id;
	}

	public void updateQuantity(UUID id, int quantity) {
		jdbcClient.sql("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
				.param("id", id)
				.param("quantity", quantity)
				.update();
	}

	public void deleteById(UUID id) {
		jdbcClient.sql("DELETE FROM cart_items WHERE id = :id")
				.param("id", id)
				.update();
	}

	public void clearByUserId(UUID userId) {
		jdbcClient.sql("DELETE FROM cart_items WHERE user_id = :userId")
				.param("userId", userId)
				.update();
	}

	public void clearBySessionId(String sessionId) {
		jdbcClient.sql("DELETE FROM cart_items WHERE session_id = :sessionId")
				.param("sessionId", sessionId)
				.update();
	}

	public void mergeSessionToUser(String sessionId, UUID userId) {
		for (CartItem guestItem : findBySessionId(sessionId)) {
			var existing = findUserItemForGame(userId, guestItem.getGameId());
			if (existing.isPresent()) {
				int mergedQty = Math.min(10, existing.get().getQuantity() + guestItem.getQuantity());
				updateQuantity(existing.get().getId(), mergedQty);
				deleteById(guestItem.getId());
			}
			else {
				jdbcClient.sql("""
						UPDATE cart_items SET user_id = :userId, session_id = NULL
						WHERE id = :id
						""")
						.param("userId", userId)
						.param("id", guestItem.getId())
						.update();
			}
		}
	}

	private Optional<CartItem> findUserItemForGame(UUID userId, UUID gameId) {
		return jdbcClient.sql("""
				SELECT ci.id, ci.user_id, ci.session_id, ci.game_id, ci.quantity, ci.created_at,
				       g.id AS g_id, g.title, g.slug, g.cover_image_url, g.platform,
				       g.base_price, g.discounted_price, g.discount_end_date, g.status
				FROM cart_items ci
				INNER JOIN games g ON g.id = ci.game_id
				WHERE ci.user_id = :userId AND ci.game_id = :gameId
				""")
				.param("userId", userId)
				.param("gameId", gameId)
				.query(this::mapCartItemRow)
				.optional();
	}

	private CartItem mapCartItemRow(ResultSet rs, int rowNum) throws SQLException {
		CartItem item = new CartItem();
		item.setId(UUID.fromString(rs.getString("id")));
		String userId = rs.getString("user_id");
		if (userId != null) {
			item.setUserId(UUID.fromString(userId));
		}
		item.setSessionId(rs.getString("session_id"));
		item.setGameId(UUID.fromString(rs.getString("game_id")));
		item.setQuantity(rs.getInt("quantity"));
		item.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

		Game game = new Game();
		game.setId(UUID.fromString(rs.getString("g_id")));
		game.setTitle(rs.getString("title"));
		game.setSlug(rs.getString("slug"));
		game.setCoverImageUrl(rs.getString("cover_image_url"));
		String platform = rs.getString("platform");
		if (platform != null) {
			game.setPlatform(Platform.valueOf(platform));
		}
		game.setBasePrice(rs.getBigDecimal("base_price"));
		game.setDiscountedPrice(rs.getBigDecimal("discounted_price"));
		game.setDiscountEndDate(rs.getObject("discount_end_date", LocalDateTime.class));
		item.setGame(game);
		return item;
	}
}
