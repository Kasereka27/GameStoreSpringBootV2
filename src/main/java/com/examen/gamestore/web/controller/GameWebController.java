package com.examen.gamestore.web.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.service.GameService;
import com.examen.gamestore.service.GenreService;
import com.examen.gamestore.service.TagService;
import com.examen.gamestore.web.dto.GameForm;

import jakarta.validation.Valid;

@Controller
public class GameWebController {

	private final GameService gameService;
	private final GenreService genreService;
	private final TagService tagService;

	public GameWebController(GameService gameService, GenreService genreService, TagService tagService) {
		this.gameService = gameService;
		this.genreService = genreService;
		this.tagService = tagService;
	}

	@GetMapping("/")
	public String list(Model model) {
		model.addAttribute("games", gameService.findAll());
		return "index";
	}

	@GetMapping("/games/new")
	public String createForm(Model model) {
		prepareFormModel(model, new GameForm(), null);
		return "game-form";
	}

	@GetMapping("/games/{id}/edit")
	public String editForm(@PathVariable UUID id, Model model) {
		prepareFormModel(model, gameService.toForm(gameService.getById(id)), id);
		return "game-form";
	}

	@PostMapping("/games")
	public String create(@Valid @ModelAttribute("gameForm") GameForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			prepareFormModel(model, form, null);
			return "game-form";
		}
		try {
			gameService.create(form);
			redirectAttributes.addFlashAttribute("message", "Jeu cree.");
			return "redirect:/";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			prepareFormModel(model, form, null);
			return "game-form";
		}
	}

	@PostMapping("/games/{id}")
	public String update(@PathVariable UUID id,
			@Valid @ModelAttribute("gameForm") GameForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			prepareFormModel(model, form, id);
			return "game-form";
		}
		try {
			gameService.update(id, form);
			redirectAttributes.addFlashAttribute("message", "Jeu mis a jour.");
			return "redirect:/";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			prepareFormModel(model, form, id);
			return "game-form";
		}
	}

	@PostMapping("/games/{id}/delete")
	public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		gameService.delete(id);
		redirectAttributes.addFlashAttribute("message", "Jeu supprime.");
		return "redirect:/";
	}

	private void prepareFormModel(Model model, GameForm form, UUID gameId) {
		model.addAttribute("gameForm", form);
		model.addAttribute("gameId", gameId);
		model.addAttribute("genres", genreService.findAll());
		model.addAttribute("tags", tagService.findAll());
	}
}
