package com.examen.gamestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI gameStoreOpenApi() {
		final String bearerScheme = "bearerAuth";
		return new OpenAPI()
				.info(new Info()
						.title("GameStore API")
						.description("API REST GameStore — catalogue, panier, commandes et administration JWT")
						.version("1.0"))
				.addSecurityItem(new SecurityRequirement().addList(bearerScheme))
				.components(new Components()
						.addSecuritySchemes(bearerScheme, new SecurityScheme()
								.name(bearerScheme)
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
