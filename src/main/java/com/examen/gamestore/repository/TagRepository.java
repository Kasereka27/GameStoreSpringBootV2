package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.Tag;

@Repository
public class TagRepository {

	private final JdbcClient jdbcClient;

	public TagRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public List<Tag> findAll() {
		return jdbcClient.sql("SELECT id, slug, label FROM tags ORDER BY label")
				.query(this::mapRow)
				.list();
	}

	public Optional<Tag> findById(UUID id) {
		return jdbcClient.sql("SELECT id, slug, label FROM tags WHERE id = :id")
				.param("id", id)
				.query(this::mapRow)
				.optional();
	}

	public Optional<Tag> findBySlug(String slug) {
		return jdbcClient.sql("SELECT id, slug, label FROM tags WHERE slug = :slug")
				.param("slug", slug)
				.query(this::mapRow)
				.optional();
	}

	public boolean existsBySlug(String slug) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM tags WHERE slug = :slug")
				.param("slug", slug)
				.query(Long.class)
				.single();
		return count != null && count > 0;
	}

	public UUID insert(String slug, String label) {
		UUID id = UUID.randomUUID();
		jdbcClient.sql("INSERT INTO tags (id, slug, label) VALUES (:id, :slug, :label)")
				.param("id", id)
				.param("slug", slug)
				.param("label", label)
				.update();
		return id;
	}

	public void update(UUID id, String slug, String label) {
		jdbcClient.sql("UPDATE tags SET slug = :slug, label = :label WHERE id = :id")
				.param("id", id)
				.param("slug", slug)
				.param("label", label)
				.update();
	}

	public long countGamesByTagId(UUID tagId) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM game_tags WHERE tag_id = :id")
				.param("id", tagId)
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public void deleteById(UUID id) {
		jdbcClient.sql("DELETE FROM tags WHERE id = :id")
				.param("id", id)
				.update();
	}

	private Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
		Tag tag = new Tag();
		tag.setId(UUID.fromString(rs.getString("id")));
		tag.setSlug(rs.getString("slug"));
		tag.setLabel(rs.getString("label"));
		return tag;
	}
}
