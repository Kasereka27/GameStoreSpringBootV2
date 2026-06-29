package com.examen.gamestore.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
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

	public List<User> findAll(String query, UserRole role, Boolean enabled, int limit, int offset) {
		var sql = new StringBuilder("""
				SELECT id, email, password_hash, first_name, last_name, role,
				       enabled, email_verified, created_at, updated_at
				FROM users WHERE 1=1
				""");
		if (query != null && !query.isBlank()) {
			sql.append(" AND (LOWER(email) LIKE :q OR LOWER(first_name) LIKE :q OR LOWER(last_name) LIKE :q)");
		}
		if (role != null) {
			sql.append(" AND role = :role");
		}
		if (enabled != null) {
			sql.append(" AND enabled = :enabled");
		}
		sql.append(" ORDER BY created_at DESC LIMIT :limit OFFSET :offset");

		var spec = jdbcClient.sql(sql.toString())
				.param("limit", limit)
				.param("offset", offset);
		if (query != null && !query.isBlank()) {
			spec = spec.param("q", "%" + query.toLowerCase() + "%");
		}
		if (role != null) {
			spec = spec.param("role", role.name());
		}
		if (enabled != null) {
			spec = spec.param("enabled", enabled);
		}
		return spec.query(this::mapUser).list();
	}

	public long countAll(String query, UserRole role, Boolean enabled) {
		var sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1");
		if (query != null && !query.isBlank()) {
			sql.append(" AND (LOWER(email) LIKE :q OR LOWER(first_name) LIKE :q OR LOWER(last_name) LIKE :q)");
		}
		if (role != null) {
			sql.append(" AND role = :role");
		}
		if (enabled != null) {
			sql.append(" AND enabled = :enabled");
		}
		var spec = jdbcClient.sql(sql.toString());
		if (query != null && !query.isBlank()) {
			spec = spec.param("q", "%" + query.toLowerCase() + "%");
		}
		if (role != null) {
			spec = spec.param("role", role.name());
		}
		if (enabled != null) {
			spec = spec.param("enabled", enabled);
		}
		Long count = spec.query(Long.class).single();
		return count != null ? count : 0L;
	}

	public void updateAdminFields(UUID id, UserRole role, boolean enabled, boolean emailVerified) {
		jdbcClient.sql("""
				UPDATE users SET role = :role, enabled = :enabled, email_verified = :emailVerified,
					updated_at = CURRENT_TIMESTAMP
				WHERE id = :id
				""")
				.param("id", id)
				.param("role", role.name())
				.param("enabled", enabled)
				.param("emailVerified", emailVerified)
				.update();
	}

	public long countByRole(UserRole role) {
		Long count = jdbcClient.sql("SELECT COUNT(*) FROM users WHERE role = :role AND enabled = TRUE")
				.param("role", role.name())
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public void setEnabled(UUID id, boolean enabled) {
		jdbcClient.sql("""
				UPDATE users SET enabled = :enabled, updated_at = CURRENT_TIMESTAMP
				WHERE id = :id
				""")
				.param("id", id)
				.param("enabled", enabled)
				.update();
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
