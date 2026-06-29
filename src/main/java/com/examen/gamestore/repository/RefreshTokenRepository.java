package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.RefreshToken;

@Repository
public class RefreshTokenRepository {

	private final JdbcClient jdbcClient;

	public RefreshTokenRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public void save(UUID id, UUID userId, String tokenHash, LocalDateTime expiresAt) {
		jdbcClient.sql("""
				INSERT INTO refresh_tokens (id, user_id, token_hash, expires_at, revoked, created_at)
				VALUES (:id, :userId, :tokenHash, :expiresAt, FALSE, CURRENT_TIMESTAMP)
				""")
				.param("id", id)
				.param("userId", userId)
				.param("tokenHash", tokenHash)
				.param("expiresAt", expiresAt)
				.update();
	}

	public Optional<RefreshToken> findActiveByTokenHash(String tokenHash) {
		return jdbcClient.sql("""
				SELECT id, user_id, token_hash, expires_at, revoked, created_at
				FROM refresh_tokens
				WHERE token_hash = :tokenHash AND revoked = FALSE AND expires_at > CURRENT_TIMESTAMP
				""")
				.param("tokenHash", tokenHash)
				.query(this::mapRow)
				.optional();
	}

	public void revokeByTokenHash(String tokenHash) {
		jdbcClient.sql("""
				UPDATE refresh_tokens SET revoked = TRUE
				WHERE token_hash = :tokenHash
				""")
				.param("tokenHash", tokenHash)
				.update();
	}

	public void revokeById(UUID id) {
		jdbcClient.sql("""
				UPDATE refresh_tokens SET revoked = TRUE
				WHERE id = :id
				""")
				.param("id", id)
				.update();
	}

	private RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
		RefreshToken token = new RefreshToken();
		token.setId(UUID.fromString(rs.getString("id")));
		token.setUserId(UUID.fromString(rs.getString("user_id")));
		token.setTokenHash(rs.getString("token_hash"));
		token.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
		token.setRevoked(rs.getBoolean("revoked"));
		token.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		return token;
	}
}
