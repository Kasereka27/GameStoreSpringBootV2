package com.examen.gamestore.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.util.SlugUtils;
import com.examen.gamestore.web.dto.GameForm;

@Component
public class GameMapper {

	public GameForm toForm(Game game, List<String> genreSlugs, List<String> tagSlugs) {
		GameForm form = new GameForm();
		form.setTitle(game.getTitle());
		form.setSlug(game.getSlug());
		form.setDescription(game.getDescription());
		form.setPrice(game.getPrice());
		form.setPlatform(game.getPlatform());
		form.setGenreSlugs(genreSlugs);
		form.setTagSlugs(tagSlugs);
		return form;
	}

	public Game toEntity(GameForm form, String slug) {
		Game game = new Game();
		game.setTitle(form.getTitle());
		game.setSlug(slug);
		game.setDescription(form.getDescription());
		game.setPrice(form.getPrice());
		game.setPlatform(form.getPlatform());
		return game;
	}

	public String resolveSlug(GameForm form) {
		if (form.getSlug() != null && !form.getSlug().isBlank()) {
			return form.getSlug();
		}
		return SlugUtils.toSlug(form.getTitle());
	}
}
