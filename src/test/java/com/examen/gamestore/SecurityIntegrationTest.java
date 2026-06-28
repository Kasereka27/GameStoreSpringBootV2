package com.examen.gamestore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void adminAreaRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/admin/games"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void adminCanLogin() throws Exception {
		mockMvc.perform(post("/login")
						.with(csrf())
						.param("email", "admin@gamestore.local")
						.param("password", "Admin123!"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	void authenticatedUserCanAccessAccount() throws Exception {
		var session = new MockHttpSession();
		mockMvc.perform(post("/login")
						.session(session)
						.with(csrf())
						.param("email", "demo@gamestore.local")
						.param("password", "Demo1234!"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/compte/profil").session(session))
				.andExpect(status().isOk());
	}
}
