package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.exception.EmailAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetails;
import com.examen.gamestore.service.OrderService;
import com.examen.gamestore.service.UserService;
import com.examen.gamestore.web.dto.request.ChangePasswordForm;
import com.examen.gamestore.web.dto.request.ProfileForm;
import com.examen.gamestore.web.mapper.UserMapper;

import jakarta.validation.Valid;

@Controller
public class AccountController {

	private final UserService userService;
	private final UserMapper userMapper;
	private final OrderService orderService;

	public AccountController(UserService userService, UserMapper userMapper, OrderService orderService) {
		this.userService = userService;
		this.userMapper = userMapper;
		this.orderService = orderService;
	}

	@GetMapping({"/compte", "/compte/profil"})
	public String profile(@AuthenticationPrincipal UserDetails principal, Model model) {
		return accountPage(principal, model, "profile");
	}

	@GetMapping("/compte/bibliotheque")
	public String library(@AuthenticationPrincipal UserDetails principal, Model model) {
		populateAccountModel(principal, model, "library");
		model.addAttribute("library", orderService.getLibrary(resolveUserId(principal)));
		return "account";
	}

	@GetMapping("/compte/commandes")
	public String orders(@AuthenticationPrincipal UserDetails principal, Model model) {
		populateAccountModel(principal, model, "orders");
		model.addAttribute("orders", orderService.getOrderSummaries(resolveUserId(principal)));
		return "account";
	}

	@PostMapping("/compte/profil")
	public String updateProfile(
			@AuthenticationPrincipal UserDetails principal,
			@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			populateAccountModel(principal, model, "profile");
			return "account";
		}

		try {
			userService.updateProfile(resolveUserId(principal), profileForm);
			redirectAttributes.addFlashAttribute("profileSuccess", true);
			return "redirect:/compte/profil";
		}
		catch (EmailAlreadyExistsException ex) {
			bindingResult.rejectValue("email", "email.exists", ex.getMessage());
			populateAccountModel(principal, model, "profile");
			return "account";
		}
	}

	@PostMapping("/compte/securite/mot-de-passe")
	public String changePassword(
			@AuthenticationPrincipal UserDetails principal,
			@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			populateAccountModel(principal, model, "security");
			return "account";
		}

		try {
			userService.changePassword(resolveUserId(principal), form);
			redirectAttributes.addFlashAttribute("passwordChangeSuccess", true);
			return "redirect:/compte/profil#security";
		}
		catch (IllegalArgumentException ex) {
			bindingResult.rejectValue("currentPassword", "password.invalid", ex.getMessage());
			populateAccountModel(principal, model, "security");
			return "account";
		}
	}

	private String accountPage(UserDetails principal, Model model, String activeSection) {
		populateAccountModel(principal, model, activeSection);
		return "account";
	}

	private void populateAccountModel(UserDetails principal, Model model, String activeSection) {
		var user = userService.getById(resolveUserId(principal));
		model.addAttribute("user", user);
		model.addAttribute("activeSection", activeSection);

		if (!model.containsAttribute("profileForm")) {
			model.addAttribute("profileForm", userMapper.toProfileForm(user));
		}

		if (!model.containsAttribute("changePasswordForm")) {
			model.addAttribute("changePasswordForm", new ChangePasswordForm());
		}
	}

	private UUID resolveUserId(UserDetails principal) {
		if (principal instanceof com.examen.gamestore.infrastructure.security.GameStoreUserDetails gsu) {
			return gsu.getUser().getId();
		}
		return userService.getByEmail(principal.getUsername()).getId();
	}
}
