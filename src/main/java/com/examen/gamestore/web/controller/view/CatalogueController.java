package com.examen.gamestore.web.controller.view;

import java.math.BigDecimal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.GameReviewService;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.request.ReviewForm;
import com.examen.gamestore.web.mapper.GameSearchCriteriaMapper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CatalogueController {

	private final GameService gameService;
	private final GameReviewService reviewService;
	private final GameSearchCriteriaMapper searchCriteriaMapper;

	public CatalogueController(
			GameService gameService,
			GameReviewService reviewService,
			GameSearchCriteriaMapper searchCriteriaMapper) {
		this.gameService = gameService;
		this.reviewService = reviewService;
		this.searchCriteriaMapper = searchCriteriaMapper;
	}

	@GetMapping({"/catalogue", "/promotions"})
	public String catalogue(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) String[] genre,
			@RequestParam(required = false) Platform platform,
			@RequestParam(required = false) BigDecimal priceMin,
			@RequestParam(required = false) BigDecimal priceMax,
			@RequestParam(defaultValue = "relevance") String sort,
			@RequestParam(defaultValue = "1") int page,
			HttpServletRequest request,
			Model model) {

		boolean promoOnly = request.getRequestURI().endsWith("/promotions");

		var criteria = searchCriteriaMapper.fromCatalogueParams(
				q, genre, platform, priceMin, priceMax, sort, page, promoOnly);

		var gamePage = gameService.searchGames(criteria);

		model.addAttribute("activePage", promoOnly ? "promotions" : "catalogue");
		model.addAttribute("games", gamePage.games());
		model.addAttribute("totalResults", gamePage.totalResults());
		model.addAttribute("page", gamePage);
		model.addAttribute("genres", gameService.getAllGenres());
		model.addAttribute("platforms", Platform.values());
		model.addAttribute("currentSort", sort);
		model.addAttribute("filters", criteria);
		model.addAttribute("promoOnly", promoOnly);
		model.addAttribute("cataloguePath", promoOnly ? "/promotions" : "/catalogue");

		return "catalogue";
	}

	@GetMapping("/jeu/{slug}")
	public String gameDetail(
			@org.springframework.web.bind.annotation.PathVariable String slug,
			@AuthenticationPrincipal GameStoreUserDetails user,
			Model model) {

		var game = gameService.getGameBySlug(slug);
		var reviews = reviewService.getReviewsForGame(game.getId(), 20);
		var distribution = reviewService.getRatingDistribution(game.getId());
		var similarGames = gameService.getSimilarGames(game.getId(), 4);

		model.addAttribute("activePage", "catalogue");
		model.addAttribute("game", game);
		model.addAttribute("reviews", reviews);
		model.addAttribute("ratingDistribution", distribution);
		model.addAttribute("similarGames", similarGames);
		model.addAttribute("reviewForm", new ReviewForm());
		model.addAttribute("pageTitle", game.getTitle() + " — GameStore Platform");
		model.addAttribute("pageDescription", game.getShortDescription());

		if (user != null) {
			model.addAttribute("userAlreadyReviewed",
					reviewService.getUserReview(game.getId(), user.getUser().getId()).isPresent());
		}
		else {
			model.addAttribute("userAlreadyReviewed", false);
		}

		return "game-detail";
	}
}
