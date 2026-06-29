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
import org.springframework.test.web.servlet.MvcResult;

import com.examen.gamestore.support.ApiTestJson;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderApiIntegrationTest {

	private static final String ELDEN_RING_ID = "c3000001-0000-4000-8000-000000000002";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void authenticatedUserCanCheckoutAndViewOrder() throws Exception {
		String token = loginAndGetAccessToken();

		mockMvc.perform(post("/api/cart/items")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"gameId": "%s"}
								""".formatted(ELDEN_RING_ID)))
				.andExpect(status().isOk());

		MvcResult checkoutResult = mockMvc.perform(post("/api/orders/checkout")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "Alex",
								  "lastName": "Martin",
								  "email": "demo@gamestore.local",
								  "address": "1 rue du Jeu",
								  "postalCode": "69001",
								  "city": "Lyon",
								  "country": "FR",
								  "paymentMethod": "card"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderNumber").isNotEmpty())
				.andExpect(jsonPath("$.items").isArray())
				.andReturn();

		String orderId = ApiTestJson.field(checkoutResult.getResponse().getContentAsString(), "id");

		mockMvc.perform(get("/api/orders")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == '" + orderId + "')]").exists());

		mockMvc.perform(get("/api/orders/" + orderId)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].licenseKey").isNotEmpty());
	}

	@Test
	void orderDetailForbiddenForOtherUser() throws Exception {
		String demoToken = loginAndGetAccessToken();
		String adminToken = loginAsAdmin();

		mockMvc.perform(post("/api/cart/items")
						.header("Authorization", "Bearer " + demoToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"gameId": "%s"}
								""".formatted(ELDEN_RING_ID)))
				.andExpect(status().isOk());

		MvcResult checkoutResult = mockMvc.perform(post("/api/orders/checkout")
						.header("Authorization", "Bearer " + demoToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(checkoutBody()))
				.andExpect(status().isCreated())
				.andReturn();

		String orderId = ApiTestJson.field(checkoutResult.getResponse().getContentAsString(), "id");

		mockMvc.perform(get("/api/orders/" + orderId)
						.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isNotFound());
	}

	@Test
	void libraryListsPurchasedGames() throws Exception {
		String token = loginAndGetAccessToken();

		mockMvc.perform(post("/api/cart/items")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"gameId": "%s"}
								""".formatted(ELDEN_RING_ID)))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/orders/checkout")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content(checkoutBody()))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/library")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].gameId").value(ELDEN_RING_ID))
				.andExpect(jsonPath("$[0].licenseKey").isNotEmpty());

		mockMvc.perform(get("/api/library/" + ELDEN_RING_ID + "/key")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.gameId").value(ELDEN_RING_ID))
				.andExpect(jsonPath("$.licenseKey").isNotEmpty());
	}

	private String checkoutBody() {
		return """
				{
				  "firstName": "Alex",
				  "lastName": "Martin",
				  "email": "demo@gamestore.local",
				  "address": "1 rue du Jeu",
				  "postalCode": "69001",
				  "city": "Lyon",
				  "country": "FR",
				  "paymentMethod": "card"
				}
				""";
	}

	private String loginAndGetAccessToken() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "demo@gamestore.local",
								  "password": "Demo1234!"
								}
								"""))
				.andExpect(status().isOk())
				.andReturn();
		return ApiTestJson.field(loginResult.getResponse().getContentAsString(), "accessToken");
	}

	private String loginAsAdmin() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "email": "admin@gamestore.local",
								  "password": "Admin123!"
								}
								"""))
				.andExpect(status().isOk())
				.andReturn();
		return ApiTestJson.field(loginResult.getResponse().getContentAsString(), "accessToken");
	}
}
