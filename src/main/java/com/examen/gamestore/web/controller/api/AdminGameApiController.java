package com.examen.gamestore.web.controller.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.request.GameForm;
import com.examen.gamestore.web.dto.response.GameDetailResponse;
import com.examen.gamestore.web.mapper.GameApiMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/games")
public class AdminGameApiController {

	private final GameService gameService;
	private final GameApiMapper gameApiMapper;

	public AdminGameApiController(GameService gameService, GameApiMapper gameApiMapper) {
		this.gameService = gameService;
		this.gameApiMapper = gameApiMapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GameDetailResponse createGame(@Valid @RequestBody GameForm form) {
		UUID id = gameService.createGame(form);
		return gameApiMapper.toDetailResponse(gameService.getGameById(id));
	}

	@PutMapping("/{id}")
	public GameDetailResponse updateGame(@PathVariable UUID id, @Valid @RequestBody GameForm form) {
		gameService.updateGame(id, form);
		return gameApiMapper.toDetailResponse(gameService.getGameById(id));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deactivateGame(@PathVariable UUID id) {
		gameService.deactivateGame(id);
	}
}
