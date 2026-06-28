package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.domain.model.User;

@Repository
public class UserRepository {

	private final JdbcClient jdbcClient;

	public UserRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public Optional<User> findByEmail(String email) {
		return jdbcClient.sql("""
				SELECT id, email, password_hash, first_name, last_name, role,
				       enabled, email_verified, created_at, updated_at
				FROM users WHERE LOWER(email) = LOWER(:email)
				""")
				.param("email", email)
				.query(this::mapUser)
				.optional();
	}

	public Optional<User> findById(UUID id) {
		return jdbcClient.sql("""
				SELECT id, email, password_hash, first_name, last_name, role,
				       enabled, email_verified, created_at, updated_at
				FROM users WHERE id = :id
				""")
				.param("id", id)
				.query(this::mapUser)
				.optional();
	}

	public boolean existsByEmail(String email) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(:email)")
				.param("email", email)
				.query(Long.class)
				.single();
		return count != null && count > 0;
	}

	public UUID insert(User user) {
		UUID id = user.getId() != null ? user.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO users (
					id, email, password_hash, first_name, last_name,
					role, enabled, email_verified, created_at, updated_at
				) VALUES (
					:id, :email, :passwordHash, :firstName, :lastName,
					:role, :enabled, :emailVerified, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
				)
				""")
				.param("id", id)
				.param("email", user.getEmail().toLowerCase())
				.param("passwordHash", user.getPasswordHash())
				.param("firstName", user.getFirstName())
				.param("lastName", user.getLastName())
				.param("role", user.getRole().name())
				.param("enabled", user.isEnabled())
				.param("emailVerified", user.isEmailVerified())
				.update();
		return id;
	}

	public void updateProfile(UUID id, String firstName, String lastName, String email) {
		jdbcClient.sql("""
				UPDATE users SET first_name = :firstName, last_name = :lastName,
					email = :email, updated_at = CURRENT_TIMESTAMP
				WHERE id = :id
				""")
				.param("id", id)
				.param("firstName", firstName)
				.param("lastName", lastName)
				.param("email", email.toLowerCase())
				.update();
	}

	public void updatePassword(UUID id, String passwordHash) {
		jdbcClient.sql("""
				UPDATE users SET password_hash = :passwordHash, updated_at = CURRENT_TIMESTAMP
				WHERE id = :id
				""")
				.param("id", id)
				.param("passwordHash", passwordHash)
				.update();
	}

	public long countCreatedToday() {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*) FROM users WHERE created_at >= CURRENT_DATE
				""")
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	private User mapUser(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(UUID.fromString(rs.getString("id")));
		user.setEmail(rs.getString("email"));
		user.setPasswordHash(rs.getString("password_hash"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setRole(UserRole.valueOf(rs.getString("role")));
		user.setEnabled(rs.getBoolean("enabled"));
		user.setEmailVerified(rs.getBoolean("email_verified"));
		user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		user.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
		return user;
	}
}
