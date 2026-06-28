package com.examen.gamestore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.GameReview;
import com.examen.gamestore.exception.GameNotFoundException;
import com.examen.gamestore.repository.GameRepository;
import com.examen.gamestore.repository.GameReviewRepository;
import com.examen.gamestore.service.GameReviewService;
import com.examen.gamestore.web.dto.RatingDistribution;
import com.examen.gamestore.web.dto.request.ReviewForm;

@Service
public class GameReviewServiceImpl implements GameReviewService {

	private final GameReviewRepository reviewRepository;
	private final GameRepository gameRepository;

	public GameReviewServiceImpl(GameReviewRepository reviewRepository, GameRepository gameRepository) {
		this.reviewRepository = reviewRepository;
		this.gameRepository = gameRepository;
	}

	@Override
	public List<GameReview> getReviewsForGame(UUID gameId, int limit) {
		ensureGameExists(gameId);
		return reviewRepository.findByGameId(gameId, limit);
	}

	@Override
	public List<RatingDistribution> getRatingDistribution(UUID gameId) {
		ensureGameExists(gameId);
		Map<Integer, Long> counts = reviewRepository.countByRating(gameId);
		long total = counts.values().stream().mapToLong(Long::longValue).sum();
		var distribution = new ArrayList<RatingDistribution>();
		for (int stars = 5; stars >= 1; stars--) {
			long count = counts.getOrDefault(stars, 0L);
			int percentage = total == 0 ? 0 : (int) Math.round(count * 100.0 / total);
			distribution.add(new RatingDistribution(stars, count, percentage));
		}
		return distribution;
	}

	@Override
	public Optional<GameReview> getUserReview(UUID gameId, UUID userId) {
		return reviewRepository.findByGameIdAndUserId(gameId, userId);
	}

	@Override
	@Transactional
	public void submitReview(UUID gameId, UUID userId, ReviewForm form) {
		ensureGameExists(gameId);
		if (reviewRepository.findByGameIdAndUserId(gameId, userId).isPresent()) {
			throw new IllegalStateException("Vous avez déjà publié un avis pour ce jeu.");
		}

		var review = new GameReview();
		review.setGameId(gameId);
		review.setUserId(userId);
		review.setRating(form.getRating());
		review.setContent(form.getContent().trim());
		review.setVerifiedPurchase(false);
		review.setHelpfulCount(0);

		reviewRepository.insert(review);
		reviewRepository.refreshGameRatingStats(gameId);
	}

	private void ensureGameExists(UUID gameId) {
		if (gameRepository.findById(gameId).isEmpty()) {
			throw new GameNotFoundException(gameId.toString());
		}
	}
}
