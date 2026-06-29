package com.examen.gamestore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

import com.examen.gamestore.service.cart.ApiCartScopeFactory;
import com.examen.gamestore.support.ApiTestJson;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartApiIntegrationTest {

	private static final String ELDEN_RING_ID = "c3000001-0000-4000-8000-000000000002";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void guestCanAddItemAndReceiveCartSessionHeader() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/cart/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"gameId": "%s"}
								""".formatted(ELDEN_RING_ID)))
				.andExpect(status().isOk())
				.andExpect(header().exists(ApiCartScopeFactory.CART_SESSION_HEADER))
				.andExpect(jsonPath("$.itemCount").value(1))
				.andReturn();

		String cartSession = result.getResponse().getHeader(ApiCartScopeFactory.CART_SESSION_HEADER);

		mockMvc.perform(get("/api/cart").header(ApiCartScopeFactory.CART_SESSION_HEADER, cartSession))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].title").value("Elden Ring"));
	}

	@Test
	void authenticatedUserCanApplyPromoToCart() throws Exception {
		String token = loginAndGetAccessToken();

		mockMvc.perform(post("/api/cart/items")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"gameId": "%s"}
								""".formatted(ELDEN_RING_ID)))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/cart/promo")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"code": "GAME10"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.appliedPromoCode").value("GAME10"));
	}

	@Test
	void guestCanUpdateAndRemoveCartItem() throws Exception {
		MvcResult addResult = mockMvc.perform(post("/api/cart/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"gameId": "%s"}
								""".formatted(ELDEN_RING_ID)))
				.andExpect(status().isOk())
				.andReturn();

		String cartSession = addResult.getResponse().getHeader(ApiCartScopeFactory.CART_SESSION_HEADER);
		String itemId = ApiTestJson.firstCartItemId(addResult.getResponse().getContentAsString());

		mockMvc.perform(put("/api/cart/items/" + itemId)
						.header(ApiCartScopeFactory.CART_SESSION_HEADER, cartSession)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"quantity": 2}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].quantity").value(2));

		mockMvc.perform(delete("/api/cart/items/" + itemId)
						.header(ApiCartScopeFactory.CART_SESSION_HEADER, cartSession))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.itemCount").value(0));
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
}
