package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.exception.GameNotFoundException;
import com.examen.gamestore.repository.GameRepository;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.GamePage;
import com.examen.gamestore.web.dto.GameSearchCriteria;
import com.examen.gamestore.web.dto.request.GameForm;
import com.examen.gamestore.web.mapper.GameMapper;

@Service
public class GameServiceImpl implements GameService {

	private final GameRepository gameRepository;
	private final GameMapper gameMapper;

	public GameServiceImpl(GameRepository gameRepository, GameMapper gameMapper) {
		this.gameRepository = gameRepository;
		this.gameMapper = gameMapper;
	}

	@Override
	public GamePage searchGames(GameSearchCriteria criteria) {
		long total = gameRepository.count(criteria);
		List<Game> games = gameRepository.findAll(criteria);
		int totalPages = (int) Math.ceil((double) total / criteria.getPageSize());
		int from = total == 0 ? 0 : criteria.getOffset() + 1;
		int to = (int) Math.min(criteria.getOffset() + games.size(), total);

		return new GamePage(games, total, criteria.getPage(), criteria.getPageSize(), totalPages, from, to);
	}

	@Override
	public Game getGameBySlug(String slug) {
		Game game = gameRepository.findBySlug(slug)
				.orElseThrow(() -> new GameNotFoundException(slug));
		enrichGameDetails(game);
		return game;
	}

	@Override
	public Game getGameById(UUID id) {
		Game game = gameRepository.findById(id)
				.orElseThrow(() -> new GameNotFoundException(id.toString()));
		enrichGameDetails(game);
		return game;
	}

	private void enrichGameDetails(Game game) {
		game.setGenreLabels(gameRepository.findGenreLabelsByGameId(game.getId()));
		game.setTagLabels(gameRepository.findTagLabelsByGameId(game.getId()));
		game.setImages(gameRepository.findImagesByGameId(game.getId()));
	}

	@Override
	public List<Game> getFeaturedGames(int limit) {
		return gameRepository.findFeatured(limit);
	}

	@Override
	public List<Game> getBestsellers(int limit) {
		return gameRepository.findBestsellers(limit);
	}

	@Override
	public List<Genre> getAllGenres() {
		return gameRepository.findAllGenres();
	}

	@Override
	public List<Tag> getAllTags() {
		return gameRepository.findAllTags();
	}

	@Override
	public List<Game> getSimilarGames(UUID gameId, int limit) {
		return gameRepository.findSimilarGames(gameId, limit);
	}

	@Override
	public GameForm getGameForm(UUID id) {
		Game game = getGameById(id);
		return gameMapper.toForm(
				game,
				gameRepository.findGenreSlugsByGameId(id),
				gameRepository.findTagSlugsByGameId(id),
				game.getImages());
	}

	@Override
	@Transactional
	public UUID createGame(GameForm form) {
		if (gameRepository.slugExists(form.getSlug(), null)) {
			throw new IllegalArgumentException("Ce slug existe déjà : " + form.getSlug());
		}
		Game game = gameMapper.toEntity(form);
		UUID id = gameRepository.insert(game);
		saveRelations(id, form);
		return id;
	}

	@Override
	@Transactional
	public void updateGame(UUID id, GameForm form) {
		Game existing = gameRepository.findById(id)
				.orElseThrow(() -> new GameNotFoundException(id.toString()));
		if (gameRepository.slugExists(form.getSlug(), id)) {
			throw new IllegalArgumentException("Ce slug existe déjà : " + form.getSlug());
		}
		Game game = gameMapper.toEntity(form, existing);
		game.setId(id);
		gameRepository.update(game);
		saveRelations(id, form);
	}

	private void saveRelations(UUID gameId, GameForm form) {
		gameRepository.replaceGenres(gameId, form.getGenreSlugs());
		gameRepository.replaceTags(gameId, form.getTagSlugs());
		gameRepository.replaceImages(gameId, gameMapper.parseScreenshotUrls(form.getScreenshotUrls()));
	}

	@Override
	@Transactional
	public void deactivateGame(UUID id) {
		if (gameRepository.findById(id).isEmpty()) {
			throw new GameNotFoundException(id.toString());
		}
		gameRepository.deactivate(id);
	}
}
