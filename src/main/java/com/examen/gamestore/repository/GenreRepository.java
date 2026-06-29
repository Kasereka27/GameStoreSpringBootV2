package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.Genre;

@Repository
public class GenreRepository {

	private final JdbcClient jdbcClient;

	public GenreRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<Genre> findAll() {
		return jdbcClient.sql("SELECT id, slug, label FROM genres ORDER BY label")
				.query(this::mapRow)
				.list();
	}

	public Optional<Genre> findById(UUID id) {
		return jdbcClient.sql("SELECT id, slug, label FROM genres WHERE id = :id")
				.param("id", id)
				.query(this::mapRow)
				.optional();
	}

	public Optional<Genre> findBySlug(String slug) {
		return jdbcClient.sql("SELECT id, slug, label FROM genres WHERE slug = :slug")
				.param("slug", slug)
				.query(this::mapRow)
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
				.param("id", id)
				.param("slug", slug)
				.param("label", label)
				.update();
		return id;
	}

	public void update(UUID id, String slug, String label) {
		jdbcClient.sql("UPDATE genres SET slug = :slug, label = :label WHERE id = :id")
				.param("id", id)
				.param("slug", slug)
				.param("label", label)
				.update();
	}

	public long countGamesByGenreId(UUID genreId) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM game_genres WHERE genre_id = :id")
				.param("id", genreId)
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public void deleteById(UUID id) {
		jdbcClient.sql("DELETE FROM genres WHERE id = :id")
				.param("id", id)
				.update();
	}

	private Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
		Genre genre = new Genre();
		genre.setId(UUID.fromString(rs.getString("id")));
		genre.setSlug(rs.getString("slug"));
		genre.setLabel(rs.getString("label"));
		return genre;
	}
}
