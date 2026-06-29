package com.examen.gamestore.web.controller.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.examen.gamestore.domain.enums.GameStatus;
import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.web.dto.response.GameDetailResponse;
import com.examen.gamestore.web.dto.response.GamePageResponse;
import com.examen.gamestore.web.dto.response.GameResponse;
import com.examen.gamestore.web.mapper.GameApiMapper;
import com.examen.gamestore.web.mapper.GameSearchCriteriaMapper;

@RestController
@RequestMapping("/api/games")
public class GameApiController {

	private final GameService gameService;
	private final GameApiMapper gameApiMapper;
	private final GameSearchCriteriaMapper searchCriteriaMapper;

	public GameApiController(
			GameService gameService,
			GameApiMapper gameApiMapper,
			GameSearchCriteriaMapper searchCriteriaMapper) {
		this.gameService = gameService;
		this.gameApiMapper = gameApiMapper;
		this.searchCriteriaMapper = searchCriteriaMapper;
	}

	@GetMapping({ "", "/search" })
	public GamePageResponse listGames(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) String[] genre,
			@RequestParam(required = false) Platform platform,
			@RequestParam(required = false) BigDecimal priceMin,
			@RequestParam(required = false) BigDecimal priceMax,
			@RequestParam(defaultValue = "relevance") String sort,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "12") int pageSize,
			@RequestParam(defaultValue = "false") boolean promoOnly) {

		var criteria = searchCriteriaMapper.fromCatalogueParams(
				q, genre, platform, priceMin, priceMax, sort, page, promoOnly);
		criteria.setPageSize(pageSize);
		criteria.setStatus(GameStatus.ACTIVE);
		criteria.setAdminMode(false);
		return gameApiMapper.toPageResponse(gameService.searchGames(criteria));
	}

	@GetMapping("/featured")
	public List<GameResponse> featuredGames(@RequestParam(defaultValue = "10") int limit) {
		int safeLimit = Math.min(Math.max(limit, 1), 20);
		return gameService.getFeaturedGames(safeLimit).stream()
				.map(gameApiMapper::toResponse)
				.toList();
	}

	@GetMapping("/{slug}")
	public GameDetailResponse gameDetail(@PathVariable String slug) {
		return gameApiMapper.toDetailResponse(gameService.getGameBySlug(slug));
	}
}
