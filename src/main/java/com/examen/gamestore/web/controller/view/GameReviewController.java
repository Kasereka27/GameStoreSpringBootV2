package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.GameReviewService;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.request.ReviewForm;

import jakarta.validation.Valid;

@Controller
public class GameReviewController {

	private final GameService gameService;
	private final GameReviewService reviewService;

	public GameReviewController(GameService gameService, GameReviewService reviewService) {
		this.gameService = gameService;
		this.reviewService = reviewService;
	}

	@PostMapping("/jeu/{slug}/avis")
	public String submitReview(
			@PathVariable String slug,
            @Valid @ModelAttribute("reviewForm") ReviewForm reviewForm,
			BindingResult bindingResult,
			@AuthenticationPrincipal GameStoreUserDetails user,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("reviewError", "Veuillez saisir une note (1-5) et un commentaire (10 caractères minimum).");
			return "redirect:/jeu/" + slug + "#reviews-title";
		}

		var game = gameService.getGameBySlug(slug);
		UUID userId = user.getUser().getId();

		try {
			reviewService.submitReview(game.getId(), userId, reviewForm);
			redirectAttributes.addFlashAttribute("successMessage", "Votre avis a été publié.");
		}
		catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("reviewError", ex.getMessage());
		}

		return "redirect:/jeu/" + slug + "#reviews-title";
	}
}
