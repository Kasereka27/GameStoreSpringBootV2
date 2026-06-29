package com.examen.gamestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.exception.InvalidPromoCodeException;
import com.examen.gamestore.repository.UserRepository;
import com.examen.gamestore.service.cart.HttpSessionCartScope;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartServiceTest {

	private static final UUID ELDEN_RING_ID = UUID.fromString("c3000001-0000-4000-8000-000000000002");
	private static final UUID DEMO_USER_ID = UUID.fromString("e5000001-0000-4000-8000-000000000002");

	@Autowired
	private CartService cartService;

	@Autowired
	private UserRepository userRepository;

	private MockHttpSession session;

	@BeforeEach
	void setUp() {
		session = new MockHttpSession();
	}

	@Test
	void guestCanAddGameToCart() {
		cartService.addGame(ELDEN_RING_ID, new HttpSessionCartScope(session), null);
		var cart = cartService.getCart(new HttpSessionCartScope(session), null);
		assertFalse(cart.isEmpty());
		assertEquals(1, cart.getItemCount());
		assertEquals("Elden Ring", cart.getItems().getFirst().getTitle());
	}

	@Test
	void promoCodeGame10AppliesDiscount() {
		cartService.addGame(ELDEN_RING_ID, new HttpSessionCartScope(session), null);
		cartService.applyPromoCode("GAME10", new HttpSessionCartScope(session), null);
		var cart = cartService.getCart(new HttpSessionCartScope(session), null);
		assertEquals("GAME10", cart.getAppliedPromoCode());
		assertEquals(0, cart.getTotal().compareTo(cart.getSubtotal().subtract(cart.getDiscount())));
		assertTrue(cart.getDiscount().compareTo(BigDecimal.ZERO) > 0);
	}

	@Test
	void invalidPromoCodeThrows() {
		cartService.addGame(ELDEN_RING_ID, new HttpSessionCartScope(session), null);
		assertThrows(InvalidPromoCodeException.class,
				() -> cartService.applyPromoCode("INVALID", new HttpSessionCartScope(session), null));
	}

	@Test
	void loggedInUserCartPersistsByUserId() {
		assert userRepository.findById(DEMO_USER_ID).isPresent();
		cartService.addGame(ELDEN_RING_ID, new HttpSessionCartScope(session), DEMO_USER_ID);
		var cart = cartService.getCart(new HttpSessionCartScope(session), DEMO_USER_ID);
		assertEquals(1, cart.getItemCount());
	}
}
