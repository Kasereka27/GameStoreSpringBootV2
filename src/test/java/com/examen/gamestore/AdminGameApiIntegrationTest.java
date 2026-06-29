package com.examen.gamestore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.examen.gamestore.support.ApiTestJson;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminGameApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void regularUserCannotCreateGame() throws Exception {
		String token = loginAsDemo();

		mockMvc.perform(post("/api/admin/games")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(minimalGameJson("forbidden-" + UUID.randomUUID())))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminCanCreateUpdateAndDeactivateGame() throws Exception {
		String token = loginAsAdmin();
		String slug = "api-admin-game-" + UUID.randomUUID().toString().substring(0, 8);

		MvcResult createResult = mockMvc.perform(post("/api/admin/games")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(minimalGameJson(slug)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.slug").value(slug))
				.andExpect(jsonPath("$.title").value("API Admin Game"))
				.andReturn();

		String gameId = ApiTestJson.field(createResult.getResponse().getContentAsString(), "id");

		mockMvc.perform(put("/api/admin/games/" + gameId)
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "title": "API Admin Game Updated",
								  "slug": "%s",
								  "basePrice": 39.99,
								  "platform": "PC",
								  "genreSlugs": ["rpg"],
								  "tagSlugs": ["solo"]
								}
								""".formatted(slug)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("API Admin Game Updated"));

		mockMvc.perform(delete("/api/admin/games/" + gameId)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());
	}

	@Test
	void adminUpdateUnknownGameReturns404() throws Exception {
		String token = loginAsAdmin();
		String unknownId = "c3000001-0000-4000-8000-000000009999";

		mockMvc.perform(put("/api/admin/games/" + unknownId)
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(minimalGameJson("missing-game")))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	private String minimalGameJson(String slug) {
		return """
				{
				  "title": "API Admin Game",
				  "slug": "%s",
				  "basePrice": 29.99,
				  "platform": "PC",
				  "genreSlugs": ["action"],
				  "tagSlugs": ["solo"]
				}
				""".formatted(slug);
	}

	private String loginAsDemo() throws Exception {
		return login("demo@gamestore.local", "Demo1234!");
	}

	private String loginAsAdmin() throws Exception {
		return login("admin@gamestore.local", "Admin123!");
	}

	private String login(String email, String password) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "%s"
								}
								""".formatted(email, password)))
				.andExpect(status().isOk())
				.andReturn();
		return ApiTestJson.field(result.getResponse().getContentAsString(), "accessToken");
	}
}
