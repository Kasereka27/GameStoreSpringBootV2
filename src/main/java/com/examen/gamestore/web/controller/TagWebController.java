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

import com.examen.gamestore.service.TagService;
import com.examen.gamestore.web.dto.TagForm;

import jakarta.validation.Valid;

@Controller
public class TagWebController {

	private final TagService tagService;

	public TagWebController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping("/tags")
	public String list(Model model) {
		model.addAttribute("tags", tagService.findAll());
		return "tags";
	}

	@GetMapping("/tags/new")
	public String createForm(Model model) {
		model.addAttribute("tagForm", new TagForm());
		model.addAttribute("tagId", null);
		return "tag-form";
	}

	@GetMapping("/tags/{id}/edit")
	public String editForm(@PathVariable UUID id, Model model) {
		var tag = tagService.getById(id);
		var form = new TagForm();
		form.setLabel(tag.getLabel());
		form.setSlug(tag.getSlug());
		model.addAttribute("tagForm", form);
		model.addAttribute("tagId", id);
		return "tag-form";
	}

	@PostMapping("/tags")
	public String create(@Valid @ModelAttribute("tagForm") TagForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("tagId", null);
			return "tag-form";
		}
		try {
			tagService.create(form);
			redirectAttributes.addFlashAttribute("message", "Tag cree.");
			return "redirect:/tags";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("tagId", null);
			return "tag-form";
		}
	}

	@PostMapping("/tags/{id}")
	public String update(@PathVariable UUID id,
			@Valid @ModelAttribute("tagForm") TagForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("tagId", id);
			return "tag-form";
		}
		try {
			tagService.update(id, form);
			redirectAttributes.addFlashAttribute("message", "Tag mis a jour.");
			return "redirect:/tags";
		}
		catch (RuntimeException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("tagId", id);
			return "tag-form";
		}
	}

	@PostMapping("/tags/{id}/delete")
	public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			tagService.delete(id);
			redirectAttributes.addFlashAttribute("message", "Tag supprime.");
		}
		catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/tags";
	}
}
