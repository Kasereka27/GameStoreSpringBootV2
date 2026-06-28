package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.web.dto.GamePage;
import com.examen.gamestore.web.dto.GameSearchCriteria;
import com.examen.gamestore.web.dto.request.GameForm;

public interface GameService {

	GamePage searchGames(GameSearchCriteria criteria);

	Game getGameBySlug(String slug);

	Game getGameById(UUID id);

	List<Game> getFeaturedGames(int limit);

	List<Game> getBestsellers(int limit);

	List<Genre> getAllGenres();

	List<Tag> getAllTags();

	List<Game> getSimilarGames(UUID gameId, int limit);

	GameForm getGameForm(UUID id);

	UUID createGame(GameForm form);

	void updateGame(UUID id, GameForm form);

	void deactivateGame(UUID id);
}
