package com.examen.gamestore.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.PegiRating;
import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.GameImage;
import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.web.dto.GameSearchCriteria;

@Repository
public class GameRepository {

	private static final String GAME_COLUMNS = """
			g.id, g.title, g.slug, g.short_description, g.long_description,
			g.publisher, g.developer, g.release_date, g.base_price, g.discounted_price,
			g.discount_end_date, g.platform, g.pegi_rating, g.status, g.trailer_url,
			g.cover_image_url, g.min_specs, g.recommended_specs, g.supported_languages,
			g.average_rating, g.review_count, g.featured, g.bestseller,
			g.created_at, g.updated_at
			""";

	private final JdbcClient jdbcClient;

	public GameRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<Game> findAll(GameSearchCriteria criteria) {
		var sql = buildSearchSql(criteria, false);
		var params = buildSearchParams(criteria);
		return jdbcClient.sql(sql.toString())
				.params(params)
				.query(this::mapGameRow)
				.list();
	}

	public long count(GameSearchCriteria criteria) {
		var sql = buildSearchSql(criteria, true);
		var params = buildSearchParams(criteria);
		Long count = jdbcClient.sql(sql.toString())
				.params(params)
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public Optional<Game> findBySlug(String slug) {
		return jdbcClient.sql("SELECT " + GAME_COLUMNS + " FROM games g WHERE g.slug = :slug")
				.param("slug", slug)
				.query(this::mapGameRow)
				.optional();
	}

	public Optional<Game> findById(UUID id) {
		return jdbcClient.sql("SELECT " + GAME_COLUMNS + " FROM games g WHERE g.id = :id")
				.param("id", id)
				.query(this::mapGameRow)
				.optional();
	}

	public List<Game> findFeatured(int limit) {
		return jdbcClient.sql("SELECT " + GAME_COLUMNS + " FROM games g WHERE g.featured = TRUE AND g.status = 'ACTIVE' ORDER BY g.created_at DESC LIMIT :limit")
				.param("limit", limit)
				.query(this::mapGameRow)
				.list();
	}

	public List<Game> findBestsellers(int limit) {
		return jdbcClient.sql("SELECT " + GAME_COLUMNS + " FROM games g WHERE g.bestseller = TRUE AND g.status = 'ACTIVE' ORDER BY g.average_rating DESC LIMIT :limit")
				.param("limit", limit)
				.query(this::mapGameRow)
				.list();
	}

	public List<Genre> findAllGenres() {
		return jdbcClient.sql("SELECT id, slug, label FROM genres ORDER BY label")
				.query((rs, rowNum) -> {
					Genre genre = new Genre();
					genre.setId(UUID.fromString(rs.getString("id")));
					genre.setSlug(rs.getString("slug"));
					genre.setLabel(rs.getString("label"));
					return genre;
				})
				.list();
	}

	public List<Tag> findAllTags() {
		return jdbcClient.sql("SELECT id, slug, label FROM tags ORDER BY label")
				.query((rs, rowNum) -> {
					Tag tag = new Tag();
					tag.setId(UUID.fromString(rs.getString("id")));
					tag.setSlug(rs.getString("slug"));
					tag.setLabel(rs.getString("label"));
					return tag;
				})
				.list();
	}

	public List<String> findGenreSlugsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT ge.slug FROM genres ge
				INNER JOIN game_genres gg ON gg.genre_id = ge.id
				WHERE gg.game_id = :gameId
				ORDER BY ge.label
				""")
				.param("gameId", gameId)
				.query(String.class)
				.list();
	}

	public List<String> findTagSlugsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT t.slug FROM tags t
				INNER JOIN game_tags gt ON gt.tag_id = t.id
				WHERE gt.game_id = :gameId
				ORDER BY t.label
				""")
				.param("gameId", gameId)
				.query(String.class)
				.list();
	}

	public List<Game> findSimilarGames(UUID gameId, int limit) {
		return jdbcClient.sql("SELECT DISTINCT " + GAME_COLUMNS + """
				FROM games g
				INNER JOIN game_genres gg ON gg.game_id = g.id
				WHERE g.id <> :gameId
				  AND g.status = 'ACTIVE'
				  AND gg.genre_id IN (
				    SELECT genre_id FROM game_genres WHERE game_id = :gameId
				  )
				ORDER BY g.average_rating DESC, g.title ASC
				LIMIT :limit
				""")
				.param("gameId", gameId)
				.param("limit", limit)
				.query(this::mapGameRowWithoutGenres)
				.list();
	}

	public void replaceGenres(UUID gameId, List<String> genreSlugs) {
		jdbcClient.sql("DELETE FROM game_genres WHERE game_id = :gameId")
				.param("gameId", gameId)
				.update();
		if (genreSlugs == null || genreSlugs.isEmpty()) {
			return;
		}
		for (String slug : genreSlugs) {
			jdbcClient.sql("""
					INSERT INTO game_genres (game_id, genre_id)
					SELECT :gameId, id FROM genres WHERE slug = :slug
					""")
					.param("gameId", gameId)
					.param("slug", slug)
					.update();
		}
	}

	public void replaceTags(UUID gameId, List<String> tagSlugs) {
		jdbcClient.sql("DELETE FROM game_tags WHERE game_id = :gameId")
				.param("gameId", gameId)
				.update();
		if (tagSlugs == null || tagSlugs.isEmpty()) {
			return;
		}
		for (String slug : tagSlugs) {
			jdbcClient.sql("""
					INSERT INTO game_tags (game_id, tag_id)
					SELECT :gameId, id FROM tags WHERE slug = :slug
					""")
					.param("gameId", gameId)
					.param("slug", slug)
					.update();
		}
	}

	public void replaceImages(UUID gameId, List<String> urls) {
		jdbcClient.sql("DELETE FROM game_images WHERE game_id = :gameId")
				.param("gameId", gameId)
				.update();
		if (urls == null || urls.isEmpty()) {
			return;
		}
		int order = 0;
		for (String url : urls) {
			jdbcClient.sql("""
					INSERT INTO game_images (id, game_id, url, sort_order, image_type)
					VALUES (:id, :gameId, :url, :sortOrder, 'SCREENSHOT')
					""")
					.param("id", UUID.randomUUID())
					.param("gameId", gameId)
					.param("url", url)
					.param("sortOrder", order++)
					.update();
		}
	}

	public List<String> findGenreLabelsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT ge.label FROM genres ge
				INNER JOIN game_genres gg ON gg.genre_id = ge.id
				WHERE gg.game_id = :gameId
				ORDER BY ge.label
				""")
				.param("gameId", gameId)
				.query(String.class)
				.list();
	}

	public List<String> findTagLabelsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT t.label FROM tags t
				INNER JOIN game_tags gt ON gt.tag_id = t.id
				WHERE gt.game_id = :gameId
				ORDER BY t.label
				""")
				.param("gameId", gameId)
				.query(String.class)
				.list();
	}

	public List<GameImage> findImagesByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT id, game_id, url, sort_order, image_type
				FROM game_images WHERE game_id = :gameId ORDER BY sort_order
				""")
				.param("gameId", gameId)
				.query((rs, rowNum) -> {
					GameImage image = new GameImage();
					image.setId(UUID.fromString(rs.getString("id")));
					image.setGameId(UUID.fromString(rs.getString("game_id")));
					image.setUrl(rs.getString("url"));
					image.setSortOrder(rs.getInt("sort_order"));
					image.setImageType(rs.getString("image_type"));
					return image;
				})
				.list();
	}

	public UUID insert(Game game) {
		UUID id = game.getId() != null ? game.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO games (
					id, title, slug, short_description, long_description,
					publisher, developer, release_date, base_price, discounted_price,
					platform, pegi_rating, status, cover_image_url, trailer_url,
					min_specs, recommended_specs, supported_languages,
					featured, bestseller, created_at, updated_at
				) VALUES (
					:id, :title, :slug, :shortDescription, :longDescription,
					:publisher, :developer, :releaseDate, :basePrice, :discountedPrice,
					:platform, :pegiRating, :status, :coverImageUrl, :trailerUrl,
					:minSpecs, :recommendedSpecs, :supportedLanguages,
					:featured, :bestseller, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
				)
				""")
				.param("id", id)
				.param("title", game.getTitle())
				.param("slug", game.getSlug())
				.param("shortDescription", game.getShortDescription())
				.param("longDescription", game.getLongDescription())
				.param("publisher", game.getPublisher())
				.param("developer", game.getDeveloper())
				.param("releaseDate", game.getReleaseDate())
				.param("basePrice", game.getBasePrice())
				.param("discountedPrice", game.getDiscountedPrice())
				.param("platform", game.getPlatform().name())
				.param("pegiRating", game.getPegiRating() != null ? game.getPegiRating().name() : null)
				.param("status", game.getStatus().name())
				.param("coverImageUrl", game.getCoverImageUrl())
				.param("trailerUrl", game.getTrailerUrl())
				.param("minSpecs", game.getMinSpecs())
				.param("recommendedSpecs", game.getRecommendedSpecs())
				.param("supportedLanguages", game.getSupportedLanguages())
				.param("featured", game.isFeatured())
				.param("bestseller", game.isBestseller())
				.update();
		return id;
	}

	public void update(Game game) {
		jdbcClient.sql("""
				UPDATE games SET
					title = :title, slug = :slug,
					short_description = :shortDescription, long_description = :longDescription,
					publisher = :publisher, developer = :developer,
					release_date = :releaseDate, base_price = :basePrice,
					discounted_price = :discountedPrice, platform = :platform,
					pegi_rating = :pegiRating, status = :status,
					cover_image_url = :coverImageUrl, trailer_url = :trailerUrl,
					min_specs = :minSpecs, recommended_specs = :recommendedSpecs,
					supported_languages = :supportedLanguages,
					featured = :featured, bestseller = :bestseller,
					updated_at = CURRENT_TIMESTAMP
				WHERE id = :id
				""")
				.param("id", game.getId())
				.param("title", game.getTitle())
				.param("slug", game.getSlug())
				.param("shortDescription", game.getShortDescription())
				.param("longDescription", game.getLongDescription())
				.param("publisher", game.getPublisher())
				.param("developer", game.getDeveloper())
				.param("releaseDate", game.getReleaseDate())
				.param("basePrice", game.getBasePrice())
				.param("discountedPrice", game.getDiscountedPrice())
				.param("platform", game.getPlatform().name())
				.param("pegiRating", game.getPegiRating() != null ? game.getPegiRating().name() : null)
				.param("status", game.getStatus().name())
				.param("coverImageUrl", game.getCoverImageUrl())
				.param("trailerUrl", game.getTrailerUrl())
				.param("minSpecs", game.getMinSpecs())
				.param("recommendedSpecs", game.getRecommendedSpecs())
				.param("supportedLanguages", game.getSupportedLanguages())
				.param("featured", game.isFeatured())
				.param("bestseller", game.isBestseller())
				.update();
	}

	public void deactivate(UUID id) {
		jdbcClient.sql("UPDATE games SET status = 'INACTIVE', updated_at = CURRENT_TIMESTAMP WHERE id = :id")
				.param("id", id)
				.update();
	}

	public boolean slugExists(String slug, UUID excludeId) {
		var sql = new StringBuilder("SELECT COUNT(*) FROM games WHERE slug = :slug");
		var params = new java.util.HashMap<String, Object>();
		params.put("slug", slug);
		if (excludeId != null) {
			sql.append(" AND id <> :excludeId");
			params.put("excludeId", excludeId);
		}
		Long count = jdbcClient.sql(sql.toString()).params(params).query(Long.class).single();
		return count != null && count > 0;
	}

	private StringBuilder buildSearchSql(GameSearchCriteria criteria, boolean countQuery) {
		var sql = new StringBuilder();
		if (countQuery) {
			sql.append("SELECT COUNT(DISTINCT g.id) FROM games g");
		}
		else {
			sql.append("SELECT DISTINCT ").append(GAME_COLUMNS).append(" FROM games g");
		}

		if (criteria.getGenres() != null && !criteria.getGenres().isEmpty()) {
			sql.append(" INNER JOIN game_genres gg ON gg.game_id = g.id");
			sql.append(" INNER JOIN genres ge ON ge.id = gg.genre_id");
		}

		sql.append(" WHERE 1=1");

		if (criteria.getStatus() != null) {
			sql.append(" AND g.status = :status");
		}
		else if (!criteria.isAdminMode()) {
			sql.append(" AND g.status = 'ACTIVE'");
		}

		if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
			sql.append(" AND (LOWER(g.title) LIKE :query OR LOWER(g.publisher) LIKE :query OR LOWER(g.developer) LIKE :query)");
		}

		if (criteria.getGenres() != null && !criteria.getGenres().isEmpty()) {
			sql.append(" AND ge.slug IN (:genres)");
		}

		if (criteria.getPlatform() != null) {
			sql.append(" AND g.platform = :platform");
		}

		if (criteria.getPriceMin() != null) {
			sql.append(" AND COALESCE(g.discounted_price, g.base_price) >= :priceMin");
		}

		if (criteria.getPriceMax() != null) {
			sql.append(" AND COALESCE(g.discounted_price, g.base_price) <= :priceMax");
		}

		if (criteria.isPromoOnly()) {
			sql.append(" AND g.discounted_price IS NOT NULL AND g.discounted_price < g.base_price");
		}

		if (!countQuery) {
			sql.append(" ORDER BY ").append(resolveOrderBy(criteria.getSort()));
			sql.append(" LIMIT :limit OFFSET :offset");
		}

		return sql;
	}

	private java.util.Map<String, Object> buildSearchParams(GameSearchCriteria criteria) {
		var params = new java.util.HashMap<String, Object>();

		if (criteria.getStatus() != null) {
			params.put("status", criteria.getStatus().name());
		}
		if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
			params.put("query", "%" + criteria.getQuery().toLowerCase() + "%");
		}
		if (criteria.getGenres() != null && !criteria.getGenres().isEmpty()) {
			params.put("genres", criteria.getGenres());
		}
		if (criteria.getPlatform() != null) {
			params.put("platform", criteria.getPlatform().name());
		}
		if (criteria.getPriceMin() != null) {
			params.put("priceMin", criteria.getPriceMin());
		}
		if (criteria.getPriceMax() != null) {
			params.put("priceMax", criteria.getPriceMax());
		}
		params.put("limit", criteria.getPageSize());
		params.put("offset", criteria.getOffset());

		return params;
	}

	private String resolveOrderBy(String sort) {
		return switch (sort != null ? sort : "relevance") {
			case "price-asc" -> "COALESCE(g.discounted_price, g.base_price) ASC";
			case "price-desc" -> "COALESCE(g.discounted_price, g.base_price) DESC";
			case "rating" -> "g.average_rating DESC";
			case "newest" -> "g.release_date DESC";
			default -> "g.title ASC";
		};
	}

	private Game mapGameRow(ResultSet rs, int rowNum) throws SQLException {
		Game game = mapGameRowCore(rs);
		game.setGenreLabels(findGenreLabelsByGameId(game.getId()));
		return game;
	}

	private Game mapGameRowWithoutGenres(ResultSet rs, int rowNum) throws SQLException {
		return mapGameRowCore(rs);
	}

	private Game mapGameRowCore(ResultSet rs) throws SQLException {
		Game game = new Game();
		game.setId(UUID.fromString(rs.getString("id")));
		game.setTitle(rs.getString("title"));
		game.setSlug(rs.getString("slug"));
		game.setShortDescription(rs.getString("short_description"));
		game.setLongDescription(rs.getString("long_description"));
		game.setPublisher(rs.getString("publisher"));
		game.setDeveloper(rs.getString("developer"));

		var releaseDate = rs.getObject("release_date", LocalDate.class);
		game.setReleaseDate(releaseDate);

		game.setBasePrice(rs.getBigDecimal("base_price"));
		game.setDiscountedPrice(rs.getBigDecimal("discounted_price"));
		game.setDiscountEndDate(rs.getObject("discount_end_date", LocalDateTime.class));

		String platform = rs.getString("platform");
		if (platform != null) {
			game.setPlatform(Platform.valueOf(platform));
		}

		String pegi = rs.getString("pegi_rating");
		if (pegi != null) {
			game.setPegiRating(PegiRating.valueOf(pegi));
		}

		game.setStatus(GameStatus.fromString(rs.getString("status")));
		game.setTrailerUrl(rs.getString("trailer_url"));
		game.setCoverImageUrl(rs.getString("cover_image_url"));
		game.setMinSpecs(rs.getString("min_specs"));
		game.setRecommendedSpecs(rs.getString("recommended_specs"));
		game.setSupportedLanguages(rs.getString("supported_languages"));
		game.setAverageRating(rs.getBigDecimal("average_rating"));
		game.setReviewCount(rs.getInt("review_count"));
		game.setFeatured(rs.getBoolean("featured"));
		game.setBestseller(rs.getBoolean("bestseller"));
		game.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		game.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
		return game;
	}
}
