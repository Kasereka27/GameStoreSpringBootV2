package com.examen.gamestore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listGamesReturnsPaginatedJson() throws Exception {
		mockMvc.perform(get("/api/games"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.totalResults").isNumber())
				.andExpect(jsonPath("$.page").value(1));
	}

	@Test
	void getGameBySlugReturnsDetail() throws Exception {
		mockMvc.perform(get("/api/games/elden-ring"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.slug").value("elden-ring"))
				.andExpect(jsonPath("$.title").value("Elden Ring"));
	}

	@Test
	void unknownSlugReturns404Json() throws Exception {
		mockMvc.perform(get("/api/games/jeu-inexistant-xyz"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	void featuredGamesReturnsList() throws Exception {
		mockMvc.perform(get("/api/games/featured"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}
}
