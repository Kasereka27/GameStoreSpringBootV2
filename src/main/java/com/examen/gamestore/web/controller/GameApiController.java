package com.examen.gamestore.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.service.GenreService;
import com.examen.gamestore.service.TagService;
import com.examen.gamestore.web.dto.GameForm;
import com.examen.gamestore.web.dto.GenreForm;
import com.examen.gamestore.web.dto.TagForm;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class GameApiController {

	private final GameService gameService;
	private final GenreService genreService;
	private final TagService tagService;

	public GameApiController(GameService gameService, GenreService genreService, TagService tagService) {
		this.gameService = gameService;
		this.genreService = genreService;
		this.tagService = tagService;
	}

	@GetMapping("/games")
	public List<Game> listGames() {
		return gameService.findAll();
	}

	@GetMapping("/games/{id}")
	public Game getGame(@PathVariable UUID id) {
		return gameService.getById(id);
	}

	@PostMapping("/games")
	public Game createGame(@Valid @RequestBody GameForm form) {
		UUID id = gameService.create(form);
		return gameService.getById(id);
	}

	@PutMapping("/games/{id}")
	public Game updateGame(@PathVariable UUID id, @Valid @RequestBody GameForm form) {
		gameService.update(id, form);
		return gameService.getById(id);
	}

	@DeleteMapping("/games/{id}")
	public void deleteGame(@PathVariable UUID id) {
		gameService.delete(id);
	}

	@GetMapping("/genres")
	public List<Genre> listGenres() {
		return genreService.findAll();
	}

	@PostMapping("/genres")
	public Genre createGenre(@Valid @RequestBody GenreForm form) {
		UUID id = genreService.create(form);
		return genreService.getById(id);
	}

	@PutMapping("/genres/{id}")
	public Genre updateGenre(@PathVariable UUID id, @Valid @RequestBody GenreForm form) {
		genreService.update(id, form);
		return genreService.getById(id);
	}

	@DeleteMapping("/genres/{id}")
	public void deleteGenre(@PathVariable UUID id) {
		genreService.delete(id);
	}

	@GetMapping("/tags")
	public List<Tag> listTags() {
		return tagService.findAll();
	}

	@PostMapping("/tags")
	public Tag createTag(@Valid @RequestBody TagForm form) {
		UUID id = tagService.create(form);
		return tagService.getById(id);
	}

	@PutMapping("/tags/{id}")
	public Tag updateTag(@PathVariable UUID id, @Valid @RequestBody TagForm form) {
		tagService.update(id, form);
		return tagService.getById(id);
	}

	@DeleteMapping("/tags/{id}")
	public void deleteTag(@PathVariable UUID id) {
		tagService.delete(id);
	}
}
