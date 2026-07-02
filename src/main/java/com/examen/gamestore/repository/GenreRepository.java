package com.examen.gamestore.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.repository.mapping.DomainRowMappers;
import com.examen.gamestore.util.JdbcUuid;

@Repository
public class GenreRepository {

	private final JdbcClient jdbcClient;
	private final DomainRowMappers rowMappers;

	public GenreRepository(JdbcClient jdbcClient, DomainRowMappers rowMappers) {
		this.jdbcClient = jdbcClient;
		this.rowMappers = rowMappers;
	}

	public List<Genre> findAll() {
		return jdbcClient.sql("SELECT id, slug, label FROM genres ORDER BY label")
				.query(rowMappers.genre())
				.list();
	}

	public Optional<Genre> findById(UUID id) {
		return jdbcClient.sql("SELECT id, slug, label FROM genres WHERE id = :id")
				.param("id", JdbcUuid.toParam(id))
				.query(rowMappers.genre())
				.optional();
	}

	public boolean existsBySlug(String slug) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM genres WHERE slug = :slug")
				.param("slug", slug)
				.query(Long.class)
				.single();
		return count != null && count > 0;
	}

	public UUID insert(String slug, String label) {
		UUID id = UUID.randomUUID();
		jdbcClient.sql("INSERT INTO genres (id, slug, label) VALUES (:id, :slug, :label)")
				.param("id", JdbcUuid.toParam(id))
				.param("slug", slug)
				.param("label", label)
				.update();
		return id;
	}

	public void update(UUID id, String slug, String label) {
		jdbcClient.sql("UPDATE genres SET slug = :slug, label = :label WHERE id = :id")
				.param("id", JdbcUuid.toParam(id))
				.param("slug", slug)
				.param("label", label)
				.update();
	}

	public long countGamesByGenreId(UUID genreId) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM game_genres WHERE genre_id = :id")
				.param("id", JdbcUuid.toParam(genreId))
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public void deleteById(UUID id) {
		jdbcClient.sql("DELETE FROM genres WHERE id = :id")
				.param("id", JdbcUuid.toParam(id))
				.update();
	}
}
