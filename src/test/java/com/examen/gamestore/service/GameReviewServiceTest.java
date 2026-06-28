package com.examen.gamestore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.repository.GameRepository;
import com.examen.gamestore.repository.GameReviewRepository;
import com.examen.gamestore.service.impl.GameReviewServiceImpl;
import com.examen.gamestore.web.dto.request.ReviewForm;

@ExtendWith(MockitoExtension.class)
class GameReviewServiceTest {

	private static final UUID GAME_ID = UUID.fromString("c3000001-0000-4000-8000-000000000001");
	private static final UUID USER_ID = UUID.fromString("e5000001-0000-4000-8000-000000000002");

	@Mock
	private GameReviewRepository reviewRepository;

	@Mock
	private GameRepository gameRepository;

	private GameReviewService reviewService;

	@BeforeEach
	void setUp() {
		reviewService = new GameReviewServiceImpl(reviewRepository, gameRepository);
	}

	@Test
	void submitReview_persistsAndRefreshesStats() {
		when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(new Game()));
		when(reviewRepository.findByGameIdAndUserId(GAME_ID, USER_ID)).thenReturn(Optional.empty());

		var form = new ReviewForm();
		form.setRating(5);
		form.setContent("Excellent jeu, je recommande vivement !");

		reviewService.submitReview(GAME_ID, USER_ID, form);

		verify(reviewRepository).insert(any());
		verify(reviewRepository).refreshGameRatingStats(GAME_ID);
	}

	@Test
	void submitReview_rejectsDuplicate() {
		when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(new Game()));
		when(reviewRepository.findByGameIdAndUserId(GAME_ID, USER_ID)).thenReturn(Optional.of(new com.examen.gamestore.domain.model.GameReview()));

		var form = new ReviewForm();
		form.setRating(4);
		form.setContent("Second avis impossible pour ce test.");

		var ex = assertThrows(IllegalStateException.class,
				() -> reviewService.submitReview(GAME_ID, USER_ID, form));

		assertEquals("Vous avez déjà publié un avis pour ce jeu.", ex.getMessage());
		verify(reviewRepository, never()).insert(any());
	}
}
