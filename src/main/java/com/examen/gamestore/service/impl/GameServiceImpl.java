package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.repository.GameRepository;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.GameForm;
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
	public List<Game> findAll() {
		return gameRepository.findAll();
	}

	@Override
	public Game getById(UUID id) {
		return gameRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Jeu introuvable : " + id));
	}

	@Override
	@Transactional
	public UUID create(GameForm form) {
		String slug = gameMapper.resolveSlug(form);
		if (gameRepository.slugExists(slug, null)) {
			throw new IllegalArgumentException("Ce slug existe deja : " + slug);
		}
		Game game = gameMapper.toEntity(form, slug);
		UUID id = gameRepository.insert(game);
		saveRelations(id, form);
		return id;
	}

	@Override
	@Transactional
	public void update(UUID id, GameForm form) {
		getById(id);
		String slug = gameMapper.resolveSlug(form);
		if (gameRepository.slugExists(slug, id)) {
			throw new IllegalArgumentException("Ce slug existe deja : " + slug);
		}
		Game game = gameMapper.toEntity(form, slug);
		game.setId(id);
		gameRepository.update(game);
		saveRelations(id, form);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		getById(id);
		gameRepository.deleteById(id);
	}

	@Override
	public GameForm toForm(Game game) {
		return gameMapper.toForm(
				game,
				gameRepository.findGenreSlugsByGameId(game.getId()),
				gameRepository.findTagSlugsByGameId(game.getId()));
	}

	private void saveRelations(UUID gameId, GameForm form) {
		gameRepository.replaceGenres(gameId, form.getGenreSlugs());
		gameRepository.replaceTags(gameId, form.getTagSlugs());
	}
}
