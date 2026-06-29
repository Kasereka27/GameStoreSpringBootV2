package com.examen.gamestore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CrudIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void adminCanListUsers() throws Exception {
		var session = loginAsAdmin();
		mockMvc.perform(get("/admin/users").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/users"));
	}

	@Test
	void adminCanUpdateUserRole() throws Exception {
		var session = loginAsAdmin();
		mockMvc.perform(post("/admin/users/e5000001-0000-4000-8000-000000000002")
						.session(session)
						.with(csrf())
						.param("role", "ROLE_USER")
						.param("enabled", "true")
						.param("emailVerified", "true"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/users/e5000001-0000-4000-8000-000000000002"));
	}

	@Test
	void adminCannotDeleteGenreLinkedToGame() throws Exception {
		var session = loginAsAdmin();
		mockMvc.perform(post("/admin/genres/a1000001-0000-4000-8000-000000000001/delete")
						.session(session)
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/genres"));
	}

	@Test
	void demoUserCanUpdateOwnReview() throws Exception {
		var session = loginAsDemo();
		mockMvc.perform(post("/jeu/elden-ring/avis/modifier")
						.session(session)
						.with(csrf())
						.param("rating", "4")
						.param("content", "Toujours excellent après mise à jour de mon avis personnel."))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/jeu/elden-ring#reviews-title"));
	}

	@Test
	void adminCanDeleteReview() throws Exception {
		var session = loginAsAdmin();
		mockMvc.perform(post("/admin/reviews/f6000001-0000-4000-8000-000000000002/delete")
						.session(session)
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/reviews"));
	}

	@Test
	@Sql("/sql/test-orders.sql")
	void demoUserCanViewOwnOrderDetail() throws Exception {
		var session = loginAsDemo();
		mockMvc.perform(get("/compte/commandes/d7000001-0000-4000-8000-000000000001").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("account-order-detail"));
	}

	@Test
	@Sql("/sql/test-orders.sql")
	void demoUserCannotViewAnotherUsersOrder() throws Exception {
		var session = loginAsDemo();
		mockMvc.perform(get("/compte/commandes/d7000001-0000-4000-8000-000000000002").session(session))
				.andExpect(status().isNotFound())
				.andExpect(view().name("404"));
	}

	@Test
	@Sql("/sql/test-promo.sql")
	void adminCanEditAndDeletePromo() throws Exception {
		var session = loginAsAdmin();
		var promoId = "e6000001-0000-4000-8000-000000000099";

		mockMvc.perform(get("/admin/promos/" + promoId + "/edit").session(session))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/promo-form"));

		mockMvc.perform(post("/admin/promos/" + promoId)
						.session(session)
						.with(csrf())
						.param("code", "TESTCRUD")
						.param("discountType", "PERCENTAGE")
						.param("discountValue", "15.00")
						.param("minOrderAmount", "0")
						.param("maxUsages", "10")
						.param("active", "true"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/promos"));

		mockMvc.perform(post("/admin/promos/" + promoId + "/delete")
						.session(session)
						.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/promos"));
	}

	private MockHttpSession loginAsAdmin() throws Exception {
		var session = new MockHttpSession();
		mockMvc.perform(post("/login")
						.session(session)
						.with(csrf())
						.param("email", "admin@gamestore.local")
						.param("password", "Admin123!"))
				.andExpect(status().is3xxRedirection());
		return session;
	}

	private MockHttpSession loginAsDemo() throws Exception {
		var session = new MockHttpSession();
		mockMvc.perform(post("/login")
						.session(session)
						.with(csrf())
						.param("email", "demo@gamestore.local")
						.param("password", "Demo1234!"))
				.andExpect(status().is3xxRedirection());
		return session;
	}
}
