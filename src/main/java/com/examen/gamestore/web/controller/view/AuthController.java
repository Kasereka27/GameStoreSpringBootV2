package com.examen.gamestore.web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.exception.EmailAlreadyExistsException;
import com.examen.gamestore.service.UserService;
import com.examen.gamestore.web.dto.request.ForgotPasswordForm;
import com.examen.gamestore.web.dto.request.RegisterForm;
import com.examen.gamestore.web.dto.request.ResetPasswordForm;

import jakarta.validation.Valid;

@Controller
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/login")
	public String login(@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout,
			Model model) {
		if (error != null) {
			model.addAttribute("loginError", true);
		}
		if (logout != null) {
			model.addAttribute("logoutSuccess", true);
		}
		return "login";
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("registerForm", new RegisterForm());
		return "register";
	}

	@PostMapping("/register")
	public String register(
			@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			return "register";
		}

		try {
			userService.register(registerForm);
			redirectAttributes.addFlashAttribute("registrationSuccess", true);
			return "redirect:/login";
		}
		catch (EmailAlreadyExistsException ex) {
			bindingResult.rejectValue("email", "email.exists", ex.getMessage());
			return "register";
		}
	}

	@GetMapping("/mot-de-passe-oublie")
	public String forgotPasswordForm(Model model) {
		model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
		return "forgot-password";
	}

	@PostMapping("/mot-de-passe-oublie")
	public String forgotPassword(
			@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			return "forgot-password";
		}

		userService.requestPasswordReset(form.getEmail());
		redirectAttributes.addFlashAttribute("resetEmailSent", true);
		return "redirect:/mot-de-passe-oublie";
	}

	@GetMapping("/reinitialisation-mot-de-passe")
	public String resetPasswordForm(@RequestParam String token, Model model) {
		var form = new ResetPasswordForm();
		form.setToken(token);
		model.addAttribute("resetPasswordForm", form);
		return "reset-password";
	}

	@PostMapping("/reinitialisation-mot-de-passe")
	public String resetPassword(
			@Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			return "reset-password";
		}

		try {
			userService.resetPassword(form);
			redirectAttributes.addFlashAttribute("passwordResetSuccess", true);
			return "redirect:/login";
		}
		catch (com.examen.gamestore.exception.InvalidTokenException ex) {
			bindingResult.reject("token", ex.getMessage());
			return "reset-password";
		}
	}

	@GetMapping("/verification-email")
	public String emailVerification() {
		return "email-verification";
	}
}
