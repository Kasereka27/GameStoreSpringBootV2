package com.examen.gamestore.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.examen.gamestore.repository.UserRepository;

@Service
public class GameStoreUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public GameStoreUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		var user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
		if (!user.isEnabled()) {
			throw new UsernameNotFoundException("Compte désactivé.");
		}
		return new GameStoreUserDetails(user);
	}
}
