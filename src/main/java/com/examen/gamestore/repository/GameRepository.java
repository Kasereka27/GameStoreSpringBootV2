package com.examen.gamestore.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.repository.mapping.DomainRowMappers;
import com.examen.gamestore.util.JdbcUuid;

@Repository
public class GameRepository {

	private static final String GAME_COLUMNS = """
			g.id, g.title, g.slug, g.description, g.price, g.platform, g.created_at
			""";

	private final JdbcClient jdbcClient;
	private final DomainRowMappers rowMappers;
	private final RowMapper<Game> gameWithRelations;

	public GameRepository(JdbcClient jdbcClient, DomainRowMappers rowMappers) {
		this.jdbcClient = jdbcClient;
		this.rowMappers = rowMappers;
		this.gameWithRelations = (rs, rowNum) -> {
			Game game = rowMappers.game().mapRow(rs, rowNum);
			game.setGenreLabels(findGenreLabelsByGameId(game.getId()));
			game.setTagLabels(findTagLabelsByGameId(game.getId()));
			return game;
		};
	}

	public List<Game> findAll() {
		return jdbcClient.sql("""
				SELECT DISTINCT %s
				FROM games g
				LEFT JOIN game_genres gg ON gg.game_id = g.id
				LEFT JOIN genres ge ON ge.id = gg.genre_id
				ORDER BY g.title ASC
				""".formatted(GAME_COLUMNS))
				.query(gameWithRelations)
				.list();
	}

	public Optional<Game> findById(UUID id) {
		return jdbcClient.sql("SELECT " + GAME_COLUMNS + " FROM games g WHERE g.id = :id")
				.param("id", JdbcUuid.toParam(id))
				.query(gameWithRelations)
				.optional();
	}

	public Optional<Game> findBySlug(String slug) {
		return jdbcClient.sql("SELECT " + GAME_COLUMNS + " FROM games g WHERE g.slug = :slug")
				.param("slug", slug)
				.query(gameWithRelations)
				.optional();
	}

	public boolean slugExists(String slug, UUID excludeId) {
		var sql = new StringBuilder("SELECT COUNT(*) FROM games WHERE slug = :slug");
		if (excludeId != null) {
			sql.append(" AND id <> :excludeId");
		}
		var query = jdbcClient.sql(sql.toString()).param("slug", slug);
		if (excludeId != null) {
			query = query.param("excludeId", JdbcUuid.toParam(excludeId));
		}
		Long count = query.query(Long.class).single();
		return count != null && count > 0;
	}

	public UUID insert(Game game) {
		UUID id = game.getId() != null ? game.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO games (id, title, slug, description, price, platform, created_at)
				VALUES (:id, :title, :slug, :description, :price, :platform, CURRENT_TIMESTAMP)
				""")
				.param("id", JdbcUuid.toParam(id))
				.param("title", game.getTitle())
				.param("slug", game.getSlug())
				.param("description", game.getDescription())
				.param("price", game.getPrice())
				.param("platform", game.getPlatform())
				.update();
		return id;
	}

	public void update(Game game) {
		jdbcClient.sql("""
				UPDATE games SET title = :title, slug = :slug, description = :description,
					price = :price, platform = :platform
				WHERE id = :id
				""")
				.param("id", JdbcUuid.toParam(game.getId()))
				.param("title", game.getTitle())
				.param("slug", game.getSlug())
				.param("description", game.getDescription())
				.param("price", game.getPrice())
				.param("platform", game.getPlatform())
				.update();
	}

	public void deleteById(UUID id) {
		jdbcClient.sql("DELETE FROM games WHERE id = :id")
				.param("id", JdbcUuid.toParam(id))
				.update();
	}

	public List<String> findGenreSlugsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT ge.slug FROM genres ge
				INNER JOIN game_genres gg ON gg.genre_id = ge.id
				WHERE gg.game_id = :gameId
				ORDER BY ge.label
				""")
				.param("gameId", JdbcUuid.toParam(gameId))
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
				.param("gameId", JdbcUuid.toParam(gameId))
				.query(String.class)
				.list();
	}

	public void replaceGenres(UUID gameId, List<String> genreSlugs) {
		jdbcClient.sql("DELETE FROM game_genres WHERE game_id = :gameId")
				.param("gameId", JdbcUuid.toParam(gameId))
				.update();
		if (genreSlugs == null || genreSlugs.isEmpty()) {
			return;
		}
		for (String slug : genreSlugs) {
			jdbcClient.sql("""
					INSERT INTO game_genres (game_id, genre_id)
					SELECT :gameId, id FROM genres WHERE slug = :slug
					""")
					.param("gameId", JdbcUuid.toParam(gameId))
					.param("slug", slug)
					.update();
		}
	}

	public void replaceTags(UUID gameId, List<String> tagSlugs) {
		jdbcClient.sql("DELETE FROM game_tags WHERE game_id = :gameId")
				.param("gameId", JdbcUuid.toParam(gameId))
				.update();
		if (tagSlugs == null || tagSlugs.isEmpty()) {
			return;
		}
		for (String slug : tagSlugs) {
			jdbcClient.sql("""
					INSERT INTO game_tags (game_id, tag_id)
					SELECT :gameId, id FROM tags WHERE slug = :slug
					""")
					.param("gameId", JdbcUuid.toParam(gameId))
					.param("slug", slug)
					.update();
		}
	}

	private List<String> findGenreLabelsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT ge.label FROM genres ge
				INNER JOIN game_genres gg ON gg.genre_id = ge.id
				WHERE gg.game_id = :gameId
				ORDER BY ge.label
				""")
				.param("gameId", JdbcUuid.toParam(gameId))
				.query(String.class)
				.list();
	}

	private List<String> findTagLabelsByGameId(UUID gameId) {
		return jdbcClient.sql("""
				SELECT t.label FROM tags t
				INNER JOIN game_tags gt ON gt.tag_id = t.id
				WHERE gt.game_id = :gameId
				ORDER BY t.label
				""")
				.param("gameId", JdbcUuid.toParam(gameId))
				.query(String.class)
				.list();
	}
}
