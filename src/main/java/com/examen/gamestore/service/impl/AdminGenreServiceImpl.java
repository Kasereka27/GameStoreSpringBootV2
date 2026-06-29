package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.repository.GenreRepository;
import com.examen.gamestore.service.AdminGenreService;
import com.examen.gamestore.util.SlugUtils;
import com.examen.gamestore.web.dto.request.GenreForm;

@Service
public class AdminGenreServiceImpl implements AdminGenreService {

	private final GenreRepository genreRepository;

	public AdminGenreServiceImpl(GenreRepository genreRepository) {
		this.genreRepository = genreRepository;
	}

	@Override
	public List<Genre> findAll() {
		return genreRepository.findAll();
	}

	@Override
	public Genre getById(UUID id) {
		return genreRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Genre introuvable."));
	}

	@Override
	@Transactional
	public UUID create(GenreForm form) {
		String slug = resolveSlug(form);
		if (genreRepository.existsBySlug(slug)) {
			throw new IllegalArgumentException("Ce slug de genre existe déjà.");
		}
		return genreRepository.insert(slug, form.getLabel());
	}

	@Override
	@Transactional
	public void update(UUID id, GenreForm form) {
		getById(id);
		String slug = resolveSlug(form);
		genreRepository.findBySlug(slug).ifPresent(existing -> {
			if (!existing.getId().equals(id)) {
				throw new IllegalArgumentException("Ce slug de genre existe déjà.");
			}
		});
		genreRepository.update(id, slug, form.getLabel());
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		getById(id);
		long linked = genreRepository.countGamesByGenreId(id);
		if (linked > 0) {
			throw new IllegalStateException("Ce genre est utilisé par " + linked + " jeu(x). Retirez-le d'abord.");
		}
		genreRepository.deleteById(id);
	}

	private String resolveSlug(GenreForm form) {
		if (form.getSlug() != null && !form.getSlug().isBlank()) {
			return form.getSlug();
		}
		return SlugUtils.toSlug(form.getLabel());
	}
}
