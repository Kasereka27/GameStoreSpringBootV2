package com.examen.gamestore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameCrudIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void apiCrudGameLifecycle() throws Exception {
		mockMvc.perform(get("/api/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").exists());

		String createBody = """
				{
				  "title": "Test Game",
				  "slug": "test-game",
				  "description": "Jeu de test",
				  "price": 19.99,
				  "platform": "PC",
				  "genreSlugs": ["action"],
				  "tagSlugs": ["indie"]
				}
				""";

		MvcResult created = mockMvc.perform(post("/api/games")
						.contentType(MediaType.APPLICATION_JSON)
						.content(createBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Test Game"))
				.andExpect(jsonPath("$.genreLabels[0]").value("Action"))
				.andReturn();

		String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

		mockMvc.perform(get("/api/games/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.slug").value("test-game"));

		String updateBody = """
				{
				  "title": "Test Game Updated",
				  "slug": "test-game-updated",
				  "description": "Mise a jour",
				  "price": 24.99,
				  "platform": "PC",
				  "genreSlugs": ["rpg"],
				  "tagSlugs": ["singleplayer"]
				}
				""";

		mockMvc.perform(put("/api/games/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Test Game Updated"));

		mockMvc.perform(delete("/api/games/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.slug == 'test-game-updated')]").isEmpty());
	}
}
