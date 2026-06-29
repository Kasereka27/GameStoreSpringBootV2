package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.service.AdminGenreService;
import com.examen.gamestore.web.dto.request.GenreForm;

import jakarta.validation.Valid;

@Controller
public class AdminGenreController {

	private final AdminGenreService adminGenreService;

	public AdminGenreController(AdminGenreService adminGenreService) {
		this.adminGenreService = adminGenreService;
	}

	@GetMapping("/admin/genres")
	public String listGenres(Model model) {
		model.addAttribute("genres", adminGenreService.findAll());
		if (!model.containsAttribute("genreForm")) {
			model.addAttribute("genreForm", new GenreForm());
		}
		return "admin/genres";
	}

	@PostMapping("/admin/genres")
	public String createGenre(
			@Valid @ModelAttribute("genreForm") GenreForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("genres", adminGenreService.findAll());
			return "admin/genres";
		}
		try {
			adminGenreService.create(form);
			redirectAttributes.addFlashAttribute("successMessage", "Genre créé.");
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("label", "error", ex.getMessage());
			model.addAttribute("genres", adminGenreService.findAll());
			return "admin/genres";
		}
		return "redirect:/admin/genres";
	}

	@GetMapping("/admin/genres/{id}/edit")
	public String editGenre(@PathVariable UUID id, Model model) {
		var genre = adminGenreService.getById(id);
		GenreForm form = new GenreForm();
		form.setLabel(genre.getLabel());
		form.setSlug(genre.getSlug());
		model.addAttribute("genre", genre);
		model.addAttribute("genreForm", form);
		return "admin/genre-form";
	}

	@PostMapping("/admin/genres/{id}")
	public String updateGenre(
			@PathVariable UUID id,
			@Valid @ModelAttribute("genreForm") GenreForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("genre", adminGenreService.getById(id));
			return "admin/genre-form";
		}
		try {
			adminGenreService.update(id, form);
			redirectAttributes.addFlashAttribute("successMessage", "Genre mis à jour.");
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("label", "error", ex.getMessage());
			model.addAttribute("genre", adminGenreService.getById(id));
			return "admin/genre-form";
		}
		return "redirect:/admin/genres";
	}

	@PostMapping("/admin/genres/{id}/delete")
	public String deleteGenre(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			adminGenreService.delete(id);
			redirectAttributes.addFlashAttribute("successMessage", "Genre supprimé.");
		}
		catch (IllegalStateException | IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/genres";
	}
}
