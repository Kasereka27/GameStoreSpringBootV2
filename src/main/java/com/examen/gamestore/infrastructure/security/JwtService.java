package com.examen.gamestore.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.examen.gamestore.domain.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	public static final String CLAIM_TYPE = "type";
	public static final String CLAIM_ROLE = "role";
	public static final String TYPE_ACCESS = "access";
	public static final String TYPE_REFRESH = "refresh";

	private final SecretKey secretKey;
	private final Duration accessTokenExpiration;
	private final Duration refreshTokenExpiration;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.access-token-expiration}") Duration accessTokenExpiration,
			@Value("${app.jwt.refresh-token-expiration}") Duration refreshTokenExpiration) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

	public String generateAccessToken(User user) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getId().toString())
				.claim(CLAIM_ROLE, user.getRole().name())
				.claim(CLAIM_TYPE, TYPE_ACCESS)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(accessTokenExpiration)))
				.signWith(secretKey)
				.compact();
	}

	public String generateRefreshToken(User user, UUID tokenId) {
		Instant now = Instant.now();
		return Jwts.builder()
				.id(tokenId.toString())
				.subject(user.getId().toString())
				.claim(CLAIM_ROLE, user.getRole().name())
				.claim(CLAIM_TYPE, TYPE_REFRESH)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(refreshTokenExpiration)))
				.signWith(secretKey)
				.compact();
	}

	public Claims parseToken(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		}
		catch (JwtException ex) {
			throw new IllegalArgumentException("Token JWT invalide ou expiré.");
		}
	}

	public boolean isAccessToken(Claims claims) {
		return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
	}

	public boolean isRefreshToken(Claims claims) {
		return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
	}

	public UUID getUserId(Claims claims) {
		return UUID.fromString(claims.getSubject());
	}

	public UUID getTokenId(Claims claims) {
		return UUID.fromString(claims.getId());
	}

	public long getAccessTokenExpiresInSeconds() {
		return accessTokenExpiration.toSeconds();
	}

	public Duration getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}
}
