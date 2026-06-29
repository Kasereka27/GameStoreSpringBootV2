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

import com.examen.gamestore.service.AdminTagService;
import com.examen.gamestore.web.dto.request.TagForm;

import jakarta.validation.Valid;

@Controller
public class AdminTagController {

	private final AdminTagService adminTagService;

	public AdminTagController(AdminTagService adminTagService) {
		this.adminTagService = adminTagService;
	}

	@GetMapping("/admin/tags")
	public String listTags(Model model) {
		model.addAttribute("tags", adminTagService.findAll());
		if (!model.containsAttribute("tagForm")) {
			model.addAttribute("tagForm", new TagForm());
		}
		return "admin/tags";
	}

	@PostMapping("/admin/tags")
	public String createTag(
			@Valid @ModelAttribute("tagForm") TagForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("tags", adminTagService.findAll());
			return "admin/tags";
		}
		try {
			adminTagService.create(form);
			redirectAttributes.addFlashAttribute("successMessage", "Tag créé.");
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("label", "error", ex.getMessage());
			model.addAttribute("tags", adminTagService.findAll());
			return "admin/tags";
		}
		return "redirect:/admin/tags";
	}

	@GetMapping("/admin/tags/{id}/edit")
	public String editTag(@PathVariable UUID id, Model model) {
		var tag = adminTagService.getById(id);
		TagForm form = new TagForm();
		form.setLabel(tag.getLabel());
		form.setSlug(tag.getSlug());
		model.addAttribute("tag", tag);
		model.addAttribute("tagForm", form);
		return "admin/tag-form";
	}

	@PostMapping("/admin/tags/{id}")
	public String updateTag(
			@PathVariable UUID id,
			@Valid @ModelAttribute("tagForm") TagForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("tag", adminTagService.getById(id));
			return "admin/tag-form";
		}
		try {
			adminTagService.update(id, form);
			redirectAttributes.addFlashAttribute("successMessage", "Tag mis à jour.");
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("label", "error", ex.getMessage());
			model.addAttribute("tag", adminTagService.getById(id));
			return "admin/tag-form";
		}
		return "redirect:/admin/tags";
	}

	@PostMapping("/admin/tags/{id}/delete")
	public String deleteTag(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		try {
			adminTagService.delete(id);
			redirectAttributes.addFlashAttribute("successMessage", "Tag supprimé.");
		}
		catch (IllegalStateException | IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/tags";
	}
}
