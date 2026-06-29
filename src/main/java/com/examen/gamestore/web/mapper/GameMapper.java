package com.examen.gamestore.web.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.GameImage;
import com.examen.gamestore.web.dto.request.GameForm;

@Component
public class GameMapper {

	public Game toEntity(GameForm form) {
		return applyForm(form, new Game());
	}

	public Game toEntity(GameForm form, Game existing) {
		return applyForm(form, existing);
	}

	public GameForm toForm(Game game) {
		var form = new GameForm();
		form.setTitle(game.getTitle());
		form.setSlug(game.getSlug());
		form.setShortDescription(game.getShortDescription());
		form.setLongDescription(game.getLongDescription());
		form.setPublisher(game.getPublisher());
		form.setDeveloper(game.getDeveloper());
		form.setReleaseDate(game.getReleaseDate());
		form.setBasePrice(game.getBasePrice());
		form.setDiscountedPrice(game.getDiscountedPrice());
		if (game.getDiscountEndDate() != null) {
			form.setDiscountEndDate(game.getDiscountEndDate().toLocalDate());
		}
		form.setPlatform(game.getPlatform());
		form.setStatus(game.getStatus());
		form.setPegiRating(game.getPegiRating());
		form.setCoverImageUrl(game.getCoverImageUrl());
		form.setTrailerUrl(game.getTrailerUrl());
		form.setMinSpecs(game.getMinSpecs());
		form.setRecommendedSpecs(game.getRecommendedSpecs());
		form.setSupportedLanguages(game.getSupportedLanguages());
		form.setFeatured(game.isFeatured());
		form.setBestseller(game.isBestseller());
		return form;
	}

	public GameForm toForm(Game game, List<String> genreSlugs, List<String> tagSlugs, List<GameImage> images) {
		var form = toForm(game);
		form.setGenreSlugs(genreSlugs);
		form.setTagSlugs(tagSlugs);
		if (images != null && !images.isEmpty()) {
			form.setScreenshotUrls(images.stream()
					.map(GameImage::getUrl)
					.collect(Collectors.joining("\n")));
		}
		return form;
	}

	public List<String> parseScreenshotUrls(String raw) {
		if (raw == null || raw.isBlank()) {
			return List.of();
		}
		return Arrays.stream(raw.split("\\r?\\n"))
				.map(String::trim)
				.filter(line -> !line.isBlank())
				.toList();
	}

	private Game applyForm(GameForm form, Game game) {
		game.setTitle(form.getTitle());
		game.setSlug(form.getSlug());
		game.setShortDescription(form.getShortDescription());
		game.setLongDescription(form.getLongDescription());
		game.setPublisher(form.getPublisher());
		game.setDeveloper(form.getDeveloper());
		game.setReleaseDate(form.getReleaseDate());
		game.setBasePrice(form.getBasePrice());
		game.setDiscountedPrice(form.getDiscountedPrice());
		if (form.getDiscountEndDate() != null) {
			game.setDiscountEndDate(form.getDiscountEndDate().atTime(23, 59, 59));
		}
		else {
			game.setDiscountEndDate(null);
		}
		game.setPlatform(form.getPlatform());
		game.setStatus(form.getStatus());
		game.setPegiRating(form.getPegiRating());
		game.setCoverImageUrl(form.getCoverImageUrl());
		game.setTrailerUrl(form.getTrailerUrl());
		game.setMinSpecs(form.getMinSpecs());
		game.setRecommendedSpecs(form.getRecommendedSpecs());
		game.setSupportedLanguages(form.getSupportedLanguages());
		game.setFeatured(form.isFeatured());
		game.setBestseller(form.isBestseller());
		return game;
	}
}
