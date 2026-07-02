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

import com.examen.gamestore.service.GenreService;
import com.examen.gamestore.web.dto.GenreForm;

import jakarta.validation.Valid;

@Controller
public class GenreWebController {

	private final GenreService genreService;

	public GenreWebController(GenreService genreService) {
		this.genreService = genreService;
	}

	@GetMapping("/genres")
	public String list(Model model) {
		model.addAttribute("genres", genreService.findAll());
		return "genres";
	}

	@GetMapping("/genres/new")
	public String createForm(Model model) {
		model.addAttribute("genreForm", new GenreForm());
		model.addAttribute("genreId", null);
		return "genre-form";
	}

	@GetMapping("/genres/{id}/edit")
	public String editForm(@PathVariable UUID id, Model model) {
		var genre = genreService.getById(id);
		var form = new GenreForm();
		form.setLabel(genre.getLabel());
		form.setSlug(genre.getSlug());
		model.addAttribute("genreForm", form);
		model.addAttribute("genreId", id);
		return "genre-form";
	}

	@PostMapping("/genres")
	public String create(@Valid @ModelAttribute("genreForm") GenreForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("genreId", null);
			return "genre-form";
		}
		try {
			genreService.create(form);
			redirectAttributes.addFlashAttribute("message", "Genre cree.");
			return "redirect:/genres";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("genreId", null);
			return "genre-form";
		}
	}

	@PostMapping("/genres/{id}")
	public String update(@PathVariable UUID id,
			@Valid @ModelAttribute("genreForm") GenreForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("genreId", id);
			return "genre-form";
		}
		try {
			genreService.update(id, form);
			redirectAttributes.addFlashAttribute("message", "Genre mis a jour.");
			return "redirect:/genres";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("genreId", id);
			return "genre-form";
		}
	}

	@PostMapping("/genres/{id}/delete")
	public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			genreService.delete(id);
			redirectAttributes.addFlashAttribute("message", "Genre supprime.");
		}
		catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/genres";
	}
}
