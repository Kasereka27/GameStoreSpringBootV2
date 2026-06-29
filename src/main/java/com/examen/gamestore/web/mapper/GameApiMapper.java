package com.examen.gamestore.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.GameImage;
import com.examen.gamestore.domain.model.User;
import com.examen.gamestore.web.dto.GamePage;
import com.examen.gamestore.web.dto.response.GameDetailResponse;
import com.examen.gamestore.web.dto.response.GameImageResponse;
import com.examen.gamestore.web.dto.response.GamePageResponse;
import com.examen.gamestore.web.dto.response.GameResponse;
import com.examen.gamestore.web.dto.response.UserResponse;

@Component
public class GameApiMapper {

	public GameResponse toResponse(Game game) {
		return new GameResponse(
				game.getId(),
				game.getTitle(),
				game.getSlug(),
				game.getShortDescription(),
				game.getBasePrice(),
				game.getEffectivePrice(),
				game.isOnPromotion(),
				game.getDiscountPercent(),
				game.getPlatform() != null ? game.getPlatform().name() : null,
				game.getCoverImageUrl(),
				game.getAverageRating(),
				game.getReviewCount(),
				game.isFeatured(),
				game.isBestseller(),
				List.copyOf(game.getGenreLabels()),
				List.copyOf(game.getTagLabels()));
	}

	public GameDetailResponse toDetailResponse(Game game) {
		return new GameDetailResponse(
				game.getId(),
				game.getTitle(),
				game.getSlug(),
				game.getShortDescription(),
				game.getLongDescription(),
				game.getPublisher(),
				game.getDeveloper(),
				game.getReleaseDate(),
				game.getBasePrice(),
				game.getEffectivePrice(),
				game.isOnPromotion(),
				game.getDiscountPercent(),
				game.getPlatform() != null ? game.getPlatform().name() : null,
				game.getPegiRating() != null ? game.getPegiRating().name() : null,
				game.getCoverImageUrl(),
				game.getTrailerUrl(),
				game.getMinSpecs(),
				game.getRecommendedSpecs(),
				game.getSupportedLanguages(),
				game.getAverageRating(),
				game.getReviewCount(),
				game.isFeatured(),
				game.isBestseller(),
				List.copyOf(game.getGenreLabels()),
				List.copyOf(game.getTagLabels()),
				game.getImages().stream().map(this::toImageResponse).toList());
	}

	public GamePageResponse toPageResponse(GamePage page) {
		return new GamePageResponse(
				page.games().stream().map(this::toResponse).toList(),
				page.totalResults(),
				page.page(),
				page.pageSize(),
				page.totalPages(),
				page.from(),
				page.to());
	}

	public UserResponse toUserResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getFirstName(),
				user.getLastName(),
				user.getRole().name(),
				user.isEmailVerified());
	}

	private GameImageResponse toImageResponse(GameImage image) {
		return new GameImageResponse(
				image.getId(),
				image.getUrl(),
				image.getSortOrder(),
				image.getImageType());
	}
}
