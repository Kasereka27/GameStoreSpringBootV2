package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.model.PasswordResetToken;

@Repository
public class PasswordResetTokenRepository {

	private final JdbcClient jdbcClient;

	public PasswordResetTokenRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public void save(PasswordResetToken token) {
		UUID id = token.getId() != null ? token.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO password_reset_tokens (id, user_id, token, expires_at, used, created_at)
				VALUES (:id, :userId, :token, :expiresAt, FALSE, CURRENT_TIMESTAMP)
				""")
				.param("id", id)
				.param("userId", token.getUserId())
				.param("token", token.getToken())
				.param("expiresAt", token.getExpiresAt())
				.update();
	}

	public Optional<PasswordResetToken> findValidByToken(String token) {
		return jdbcClient.sql("""
				SELECT id, user_id, token, expires_at, used, created_at
				FROM password_reset_tokens
				WHERE token = :token AND used = FALSE AND expires_at > CURRENT_TIMESTAMP
				""")
				.param("token", token)
				.query(this::mapToken)
				.optional();
	}

	public void markUsed(UUID id) {
		jdbcClient.sql("UPDATE password_reset_tokens SET used = TRUE WHERE id = :id")
				.param("id", id)
				.update();
	}

	public void invalidateAllForUser(UUID userId) {
		jdbcClient.sql("UPDATE password_reset_tokens SET used = TRUE WHERE user_id = :userId AND used = FALSE")
				.param("userId", userId)
				.update();
	}

	private PasswordResetToken mapToken(ResultSet rs, int rowNum) throws SQLException {
		PasswordResetToken token = new PasswordResetToken();
		token.setId(UUID.fromString(rs.getString("id")));
		token.setUserId(UUID.fromString(rs.getString("user_id")));
		token.setToken(rs.getString("token"));
		token.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
		token.setUsed(rs.getBoolean("used"));
		token.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		return token;
	}
}
