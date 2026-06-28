package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.request.GameForm;
import com.examen.gamestore.web.mapper.GameMapper;
import com.examen.gamestore.web.mapper.GameSearchCriteriaMapper;

import jakarta.validation.Valid;

@Controller
public class AdminGameController {

	private final GameService gameService;
	private final GameMapper gameMapper;
	private final GameSearchCriteriaMapper searchCriteriaMapper;

	public AdminGameController(
			GameService gameService,
			GameMapper gameMapper,
			GameSearchCriteriaMapper searchCriteriaMapper) {
		this.gameService = gameService;
		this.gameMapper = gameMapper;
		this.searchCriteriaMapper = searchCriteriaMapper;
	}

	@GetMapping("/admin/games")
	public String listGames(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) Platform platform,
			@RequestParam(required = false) GameStatus status,
			@RequestParam(defaultValue = "1") int page,
			Model model) {

		var criteria = searchCriteriaMapper.fromAdminParams(q, platform, status, page);

		var gamePage = gameService.searchGames(criteria);
		model.addAttribute("games", gamePage.games());
		model.addAttribute("totalGames", gamePage.totalResults());
		model.addAttribute("page", gamePage);
		return "admin/games";
	}

	@GetMapping("/admin/games/new")
	public String newGameForm(Model model) {
		model.addAttribute("gameForm", new GameForm());
		model.addAttribute("allGenres", gameService.getAllGenres());
		model.addAttribute("allTags", gameService.getAllTags());
		model.addAttribute("isEdit", false);
		return "admin/game-form";
	}

	@GetMapping("/admin/games/{id}/edit")
	public String editGameForm(@PathVariable UUID id, Model model) {
		model.addAttribute("gameForm", gameService.getGameForm(id));
		model.addAttribute("allGenres", gameService.getAllGenres());
		model.addAttribute("allTags", gameService.getAllTags());
		model.addAttribute("gameId", id);
		model.addAttribute("isEdit", true);
		return "admin/game-form";
	}

	@PostMapping("/admin/games")
	public String createGame(
			@Valid @ModelAttribute("gameForm") GameForm gameForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("isEdit", false);
			model.addAttribute("allGenres", gameService.getAllGenres());
			model.addAttribute("allTags", gameService.getAllTags());
			return "admin/game-form";
		}

		try {
			UUID id = gameService.createGame(gameForm);
			redirectAttributes.addFlashAttribute("successMessage", "Jeu créé avec succès.");
			return "redirect:/admin/games/" + id + "/edit";
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("slug", "slug.duplicate", ex.getMessage());
			model.addAttribute("isEdit", false);
			model.addAttribute("allGenres", gameService.getAllGenres());
			model.addAttribute("allTags", gameService.getAllTags());
			return "admin/game-form";
		}
	}

	@PostMapping("/admin/games/{id}")
	public String updateGame(
			@PathVariable UUID id,
			@Valid @ModelAttribute("gameForm") GameForm gameForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("gameId", id);
			model.addAttribute("isEdit", true);
			model.addAttribute("allGenres", gameService.getAllGenres());
			model.addAttribute("allTags", gameService.getAllTags());
			return "admin/game-form";
		}

		try {
			gameService.updateGame(id, gameForm);
			redirectAttributes.addFlashAttribute("successMessage", "Jeu mis à jour.");
			return "redirect:/admin/games";
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("slug", "slug.duplicate", ex.getMessage());
			model.addAttribute("gameId", id);
			model.addAttribute("isEdit", true);
			model.addAttribute("allGenres", gameService.getAllGenres());
			model.addAttribute("allTags", gameService.getAllTags());
			return "admin/game-form";
		}
	}

	@PostMapping("/admin/games/{id}/deactivate")
	public String deactivateGame(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		gameService.deactivateGame(id);
		redirectAttributes.addFlashAttribute("successMessage", "Jeu désactivé.");
		return "redirect:/admin/games";
	}
}
