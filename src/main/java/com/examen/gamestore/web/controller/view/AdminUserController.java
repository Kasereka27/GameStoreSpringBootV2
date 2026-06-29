package com.examen.gamestore.web.controller.view;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.domain.enums.UserRole;
import com.examen.gamestore.exception.UserNotFoundException;
import com.examen.gamestore.infrastructure.security.GameStoreUserDetails;
import com.examen.gamestore.service.AdminUserService;
import com.examen.gamestore.web.dto.request.UserAdminForm;

import jakarta.validation.Valid;

@Controller
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping("/admin/users")
	public String listUsers(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) UserRole role,
			@RequestParam(required = false) Boolean enabled,
			@RequestParam(defaultValue = "1") int page,
			Model model) {
		model.addAttribute("users", adminUserService.listUsers(q, role, enabled, page, 20));
		model.addAttribute("totalUsers", adminUserService.countUsers(q, role, enabled));
		model.addAttribute("q", q);
		model.addAttribute("roleFilter", role);
		model.addAttribute("enabledFilter", enabled);
		model.addAttribute("page", page);
		model.addAttribute("userRoles", new UserRole[] { UserRole.ROLE_USER, UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN });
		return "admin/users";
	}

	@GetMapping("/admin/users/{id}")
	public String userDetail(@PathVariable UUID id, Model model) {
		var user = adminUserService.getUser(id);
		model.addAttribute("managedUser", user);
		if (!model.containsAttribute("userAdminForm")) {
			UserAdminForm form = new UserAdminForm();
			form.setRole(user.getRole());
			form.setEnabled(user.isEnabled());
			form.setEmailVerified(user.isEmailVerified());
			model.addAttribute("userAdminForm", form);
		}
		model.addAttribute("userRoles", new UserRole[] { UserRole.ROLE_USER, UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN });
		return "admin/user-detail";
	}

	@PostMapping("/admin/users/{id}")
	public String updateUser(
			@PathVariable UUID id,
			@Valid @ModelAttribute("userAdminForm") UserAdminForm form,
			BindingResult bindingResult,
			@AuthenticationPrincipal GameStoreUserDetails currentUser,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("managedUser", adminUserService.getUser(id));
			model.addAttribute("userRoles", new UserRole[] { UserRole.ROLE_USER, UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN });
			return "admin/user-detail";
		}

		try {
			adminUserService.updateUser(id, form, currentUser.getUser().getId());
			redirectAttributes.addFlashAttribute("successMessage", "Utilisateur mis à jour.");
		}
		catch (IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		catch (UserNotFoundException ex) {
			return "redirect:/admin/users";
		}
		return "redirect:/admin/users/" + id;
	}
}
