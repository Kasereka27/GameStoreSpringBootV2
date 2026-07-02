package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.web.dto.GameForm;

public interface GameService {

	List<Game> findAll();

	Game getById(UUID id);

	UUID create(GameForm form);

	void update(UUID id, GameForm form);

	void delete(UUID id);

	GameForm toForm(Game game);
}
