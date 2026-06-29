package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.GameReview;

@Repository
public class GameReviewRepository {

	private final JdbcClient jdbcClient;

	public GameReviewRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<GameReview> findByGameId(UUID gameId, int limit) {
		return jdbcClient.sql("""
				SELECT r.id, r.game_id, r.user_id, r.rating, r.content,
				       r.verified_purchase, r.helpful_count, r.created_at,
				       u.first_name, u.last_name
				FROM game_reviews r
				INNER JOIN users u ON u.id = r.user_id
				WHERE r.game_id = :gameId
				ORDER BY r.created_at DESC
				LIMIT :limit
				""")
				.param("gameId", gameId)
				.param("limit", limit)
				.query(this::mapReviewRow)
				.list();
	}

	public Optional<GameReview> findByGameIdAndUserId(UUID gameId, UUID userId) {
		return jdbcClient.sql("""
				SELECT r.id, r.game_id, r.user_id, r.rating, r.content,
				       r.verified_purchase, r.helpful_count, r.created_at,
				       u.first_name, u.last_name
				FROM game_reviews r
				INNER JOIN users u ON u.id = r.user_id
				WHERE r.game_id = :gameId AND r.user_id = :userId
				""")
				.param("gameId", gameId)
				.param("userId", userId)
				.query(this::mapReviewRow)
				.optional();
	}

	public UUID insert(GameReview review) {
		UUID id = review.getId() != null ? review.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO game_reviews (
					id, game_id, user_id, rating, content, verified_purchase, helpful_count, created_at
				) VALUES (
					:id, :gameId, :userId, :rating, :content, :verifiedPurchase, :helpfulCount, CURRENT_TIMESTAMP
				)
				""")
				.param("id", id)
				.param("gameId", review.getGameId())
				.param("userId", review.getUserId())
				.param("rating", review.getRating())
				.param("content", review.getContent())
				.param("verifiedPurchase", review.isVerifiedPurchase())
				.param("helpfulCount", review.getHelpfulCount())
				.update();
		return id;
	}

	public Map<Integer, Long> countByRating(UUID gameId) {
		return jdbcClient.sql("""
				SELECT rating, COUNT(*) AS cnt
				FROM game_reviews
				WHERE game_id = :gameId
				GROUP BY rating
				""")
				.param("gameId", gameId)
				.query((rs, rowNum) -> Map.entry(rs.getInt("rating"), rs.getLong("cnt")))
				.list()
				.stream()
				.collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public Optional<GameReview> findById(UUID id) {
		return jdbcClient.sql("""
				SELECT r.id, r.game_id, r.user_id, r.rating, r.content,
				       r.verified_purchase, r.helpful_count, r.created_at,
				       u.first_name, u.last_name
				FROM game_reviews r
				INNER JOIN users u ON u.id = r.user_id
				WHERE r.id = :id
				""")
				.param("id", id)
				.query(this::mapReviewRow)
				.optional();
	}

	public void update(UUID id, int rating, String content) {
		jdbcClient.sql("""
				UPDATE game_reviews SET rating = :rating, content = :content WHERE id = :id
				""")
				.param("id", id)
				.param("rating", rating)
				.param("content", content)
				.update();
	}

	public void deleteById(UUID id) {
		jdbcClient.sql("DELETE FROM game_reviews WHERE id = :id")
				.param("id", id)
				.update();
	}

	public List<com.examen.gamestore.web.dto.AdminReviewView> findAllForAdmin(UUID gameId, int limit, int offset) {
		var sql = new StringBuilder("""
				SELECT r.id, r.game_id, g.title, g.slug, r.rating, r.content, r.created_at,
				       u.first_name, u.last_name
				FROM game_reviews r
				INNER JOIN games g ON g.id = r.game_id
				INNER JOIN users u ON u.id = r.user_id
				WHERE 1=1
				""");
		if (gameId != null) {
			sql.append(" AND r.game_id = :gameId");
		}
		sql.append(" ORDER BY r.created_at DESC LIMIT :limit OFFSET :offset");
		var spec = jdbcClient.sql(sql.toString())
				.param("limit", limit)
				.param("offset", offset);
		if (gameId != null) {
			spec = spec.param("gameId", gameId);
		}
		return spec.query((rs, rowNum) -> {
			var view = new com.examen.gamestore.web.dto.AdminReviewView();
			view.setId(UUID.fromString(rs.getString("id")));
			view.setGameId(UUID.fromString(rs.getString("game_id")));
			view.setGameTitle(rs.getString("title"));
			view.setGameSlug(rs.getString("slug"));
			view.setRating(rs.getInt("rating"));
			view.setContent(rs.getString("content"));
			view.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
			view.setAuthorName(rs.getString("first_name") + " " + rs.getString("last_name"));
			return view;
		}).list();
	}

	public void refreshGameRatingStats(UUID gameId) {
		jdbcClient.sql("""
				UPDATE games SET
					average_rating = COALESCE((
						SELECT ROUND(AVG(rating)::numeric, 2) FROM game_reviews WHERE game_id = :gameId
					), 0),
					review_count = (SELECT COUNT(*) FROM game_reviews WHERE game_id = :gameId),
					updated_at = CURRENT_TIMESTAMP
				WHERE id = :gameId
				""")
				.param("gameId", gameId)
				.update();
	}

	private GameReview mapReviewRow(ResultSet rs, int rowNum) throws SQLException {
		GameReview review = new GameReview();
		review.setId(UUID.fromString(rs.getString("id")));
		review.setGameId(UUID.fromString(rs.getString("game_id")));
		review.setUserId(UUID.fromString(rs.getString("user_id")));
		review.setRating(rs.getInt("rating"));
		review.setContent(rs.getString("content"));
		review.setVerifiedPurchase(rs.getBoolean("verified_purchase"));
		review.setHelpfulCount(rs.getInt("helpful_count"));
		review.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		review.setAuthorFirstName(rs.getString("first_name"));
		review.setAuthorLastName(rs.getString("last_name"));
		return review;
	}
}
