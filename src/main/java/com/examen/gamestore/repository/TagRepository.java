package com.examen.gamestore.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.repository.mapping.DomainRowMappers;

@Repository
public class TagRepository {

	private final JdbcClient jdbcClient;
	private final DomainRowMappers rowMappers;

	public TagRepository(JdbcClient jdbcClient, DomainRowMappers rowMappers) {
		this.jdbcClient = jdbcClient;
		this.rowMappers = rowMappers;
	}

	public List<Tag> findAll() {
		return jdbcClient.sql("SELECT id, slug, label FROM tags ORDER BY label")
				.query(rowMappers.tag())
				.list();
	}

	public Optional<Tag> findById(UUID id) {
		return jdbcClient.sql("SELECT id, slug, label FROM tags WHERE id = :id")
				.param("id", id)
				.query(rowMappers.tag())
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
}
