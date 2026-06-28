package com.examen.gamestore.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.examen.gamestore.domain.model.GameReview;
import com.examen.gamestore.web.dto.RatingDistribution;
import com.examen.gamestore.web.dto.request.ReviewForm;

public interface GameReviewService {

	List<GameReview> getReviewsForGame(UUID gameId, int limit);

	List<RatingDistribution> getRatingDistribution(UUID gameId);

	Optional<GameReview> getUserReview(UUID gameId, UUID userId);

	void submitReview(UUID gameId, UUID userId, ReviewForm form);
}
