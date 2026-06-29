package com.examen.gamestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetailsService;
import com.examen.gamestore.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final GameStoreUserDetailsService userDetailsService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(
			GameStoreUserDetailsService userDetailsService,
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST,
								"/api/auth/register",
								"/api/auth/login",
								"/api/auth/forgot-password",
								"/api/auth/reset-password",
								"/api/auth/refresh",
								"/api/auth/logout")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/api/auth/verify-email", "/api/games/**")
						.permitAll()
						.requestMatchers("/api/cart/**").permitAll()
						.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/jeu/*/avis").authenticated()
						.requestMatchers(
								"/", "/catalogue", "/promotions", "/jeu/**", "/a-propos", "/panier",
								"/login", "/register", "/mot-de-passe-oublie",
								"/reinitialisation-mot-de-passe/**", "/verification-email",
								"/css/**", "/js/**", "/error",
								"/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**")
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
