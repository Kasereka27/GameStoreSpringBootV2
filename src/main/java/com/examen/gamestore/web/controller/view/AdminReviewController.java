package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.service.GameReviewService;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.GameSearchCriteria;

@Controller
public class AdminReviewController {

	private final GameReviewService reviewService;
	private final GameService gameService;

	public AdminReviewController(GameReviewService reviewService, GameService gameService) {
		this.reviewService = reviewService;
		this.gameService = gameService;
	}

	@GetMapping("/admin/reviews")
	public String listReviews(
			@RequestParam(required = false) UUID gameId,
			@RequestParam(defaultValue = "1") int page,
			Model model) {
		model.addAttribute("reviews", reviewService.listReviewsForAdmin(gameId, page, 30));
		model.addAttribute("gameIdFilter", gameId);
		model.addAttribute("page", page);
		GameSearchCriteria criteria = new GameSearchCriteria();
		criteria.setAdminMode(true);
		criteria.setPageSize(50);
		model.addAttribute("games", gameService.searchGames(criteria).games());
		return "admin/reviews";
	}

	@PostMapping("/admin/reviews/{id}/delete")
	public String deleteReview(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			reviewService.deleteReviewByAdmin(id);
			redirectAttributes.addFlashAttribute("successMessage", "Avis supprimé.");
		}
		catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/reviews";
	}
}
