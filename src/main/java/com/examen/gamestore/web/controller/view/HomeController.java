package com.examen.gamestore.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.examen.gamestore.service.GameService;

@Controller
public class HomeController {

	private final GameService gameService;

	public HomeController(GameService gameService) {
		this.gameService = gameService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("activePage", "home");
		model.addAttribute("featuredGames", gameService.getFeaturedGames(3));
		model.addAttribute("bestsellers", gameService.getBestsellers(6));
		return "index";
	}
}
