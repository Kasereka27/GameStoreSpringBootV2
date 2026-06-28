package com.examen.gamestore.infrastructure.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.examen.gamestore.domain.model.User;

public class GameStoreUserDetails implements UserDetails {

	private final User user;

	public GameStoreUserDetails(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public String getDisplayName() {
		return user.getDisplayName();
	}

	public String getFirstName() {
		return user.getFirstName();
	}

	public String getLastName() {
		return user.getLastName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
}
