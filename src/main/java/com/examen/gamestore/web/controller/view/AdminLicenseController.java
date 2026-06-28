package com.examen.gamestore.web.controller.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.service.GameService;
import com.examen.gamestore.service.LicenseKeyAdminService;
import com.examen.gamestore.web.dto.GameSearchCriteria;
import com.examen.gamestore.web.dto.request.LicenseImportForm;

import jakarta.validation.Valid;

@Controller
public class AdminLicenseController {

	private final LicenseKeyAdminService licenseKeyAdminService;
	private final GameService gameService;

	public AdminLicenseController(LicenseKeyAdminService licenseKeyAdminService, GameService gameService) {
		this.licenseKeyAdminService = licenseKeyAdminService;
		this.gameService = gameService;
	}

	@GetMapping("/admin/licenses")
	public String licenses(Model model) {
		model.addAttribute("stockSummary", licenseKeyAdminService.getStockSummary());
		if (!model.containsAttribute("importForm")) {
			model.addAttribute("importForm", new LicenseImportForm());
		}
		model.addAttribute("games", listGamesForImport());
		return "admin/licenses";
	}

	private List<Game> listGamesForImport() {
		GameSearchCriteria criteria = new GameSearchCriteria();
		criteria.setAdminMode(true);
		criteria.setPageSize(50);
		return gameService.searchGames(criteria).games();
	}

	@PostMapping("/admin/licenses/import")
	public String importKeys(
			@Valid @ModelAttribute("importForm") LicenseImportForm form,
			BindingResult bindingResult,
			@RequestParam(required = false) MultipartFile csvFile,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (csvFile != null && !csvFile.isEmpty()) {
			try {
				String content = new BufferedReader(
						new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8))
						.lines()
						.collect(Collectors.joining("\n"));
				form.setKeysText(content);
			}
			catch (Exception ex) {
				bindingResult.reject("csvFile", "Impossible de lire le fichier CSV.");
			}
		}

		if (form.getKeysText() == null || form.getKeysText().isBlank()) {
			bindingResult.rejectValue("keysText", "required", "Saisissez des clés ou uploadez un fichier.");
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("stockSummary", licenseKeyAdminService.getStockSummary());
			model.addAttribute("games", listGamesForImport());
			return "admin/licenses";
		}

		int imported = licenseKeyAdminService.importKeys(form.getGameId(), form.getKeysText());
		redirectAttributes.addFlashAttribute("successMessage",
				imported + " clé(s) importée(s) avec succès.");
		return "redirect:/admin/licenses";
	}
}
