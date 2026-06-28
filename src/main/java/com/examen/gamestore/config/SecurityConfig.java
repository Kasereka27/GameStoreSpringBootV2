package com.examen.gamestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final GameStoreUserDetailsService userDetailsService;

	public SecurityConfig(GameStoreUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/jeu/*/avis").authenticated()
						.requestMatchers(
								"/", "/catalogue", "/promotions", "/jeu/**", "/a-propos", "/panier",
								"/login", "/register", "/mot-de-passe-oublie",
								"/reinitialisation-mot-de-passe/**", "/verification-email",
								"/css/**", "/js/**", "/error")
						.permitAll()
						.requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
						.requestMatchers("/compte/**", "/checkout", "/checkout/**").authenticated()
						.anyRequest().permitAll())
				.userDetailsService(userDetailsService)
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.usernameParameter("email")
						.passwordParameter("password")
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/?logout")
						.permitAll())
				.exceptionHandling(ex -> ex
						.accessDeniedPage("/403"));

		return http.build();
	}
}
