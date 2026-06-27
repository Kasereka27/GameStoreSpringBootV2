package com.examen.gamestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping({"/catalogue", "/promotions"})
	public String catalogue() {
		return "catalogue";
	}

	@GetMapping("/a-propos")
	public String aPropos() {
		return "a-propos";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/panier")
	public String cart() {
		return "cart";
	}

	@GetMapping("/checkout")
	public String checkout() {
		return "checkout";
	}

	@GetMapping({"/compte", "/compte/profil", "/compte/bibliotheque", "/compte/commandes"})
	public String account() {
		return "account";
	}

	@GetMapping("/jeu")
	public String gameDetail() {
		return "game-detail";
	}

	@GetMapping("/mot-de-passe-oublie")
	public String forgotPassword() {
		return "forgot-password";
	}

	@GetMapping("/verification-email")
	public String emailVerification() {
		return "email-verification";
	}

	@GetMapping("/maintenance")
	public String maintenance() {
		return "maintenance";
	}

	@GetMapping("/admin")
	public String adminIndex() {
		return "admin/index";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard() {
		return "admin/dashboard";
	}

	@GetMapping("/admin/games")
	public String adminGames() {
		return "admin/games";
	}

}
