package com.examen.gamestore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void registerLoginRefreshAndLogout() throws Exception {
		String email = "api-user-" + UUID.randomUUID().toString().substring(0, 8) + "@test.local";

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "Api",
								  "lastName": "User",
								  "email": "%s",
								  "password": "Test1234!",
								  "confirmPassword": "Test1234!",
								  "acceptTerms": true
								}
								""".formatted(email)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value(email));

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "%s",
								  "password": "Test1234!"
								}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andReturn();

		String loginBody = loginResult.getResponse().getContentAsString();
		String refreshToken = extractJsonString(loginBody, "refreshToken");

		MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"refreshToken": "%s"}
								""".formatted(refreshToken)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty())
				.andReturn();

		String newRefreshToken = extractJsonString(refreshResult.getResponse().getContentAsString(), "refreshToken");

		mockMvc.perform(post("/api/auth/logout")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"refreshToken": "%s"}
								""".formatted(newRefreshToken)))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"refreshToken": "%s"}
								""".formatted(newRefreshToken)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void loginWithInvalidCredentialsReturns401() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "demo@gamestore.local",
								  "password": "wrong-password"
								}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401));
	}

	@Test
	void demoUserCanLoginViaApi() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "demo@gamestore.local",
								  "password": "Demo1234!"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty());
	}

	private String extractJsonString(String json, String field) {
		String marker = "\"" + field + "\":\"";
		int start = json.indexOf(marker);
		if (start < 0) {
			throw new IllegalStateException("Field not found: " + field);
		}
		start += marker.length();
		int end = json.indexOf('"', start);
		return json.substring(start, end);
	}
}
