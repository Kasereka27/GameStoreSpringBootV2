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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminDashboardIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void adminCanAccessDashboard() throws Exception {
		var session = new MockHttpSession();
		mockMvc.perform(post("/login")
						.session(session)
						.with(csrf())
						.param("email", "admin@gamestore.local")
						.param("password", "Admin123!"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/admin/dashboard").session(session))
				.andExpect(status().isOk());
	}
}
