package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.repository.GenreRepository;
import com.examen.gamestore.service.GenreService;
import com.examen.gamestore.web.dto.GenreForm;
import com.examen.gamestore.web.mapper.GenreMapper;

@Service
public class GenreServiceImpl implements GenreService {

	private final GenreRepository genreRepository;
	private final GenreMapper genreMapper;

	public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
		this.genreRepository = genreRepository;
		this.genreMapper = genreMapper;
	}

	@Override
	public List<Genre> findAll() {
		return genreRepository.findAll();
	}

	@Override
	public Genre getById(UUID id) {
		return genreRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Genre introuvable : " + id));
	}

	@Override
	@Transactional
	public UUID create(GenreForm form) {
		String slug = genreMapper.resolveSlug(form);
		if (genreRepository.existsBySlug(slug)) {
			throw new IllegalArgumentException("Ce slug de genre existe deja.");
		}
		return genreRepository.insert(slug, form.getLabel());
	}

	@Override
	@Transactional
	public void update(UUID id, GenreForm form) {
		getById(id);
		String slug = genreMapper.resolveSlug(form);
		genreRepository.findAll().stream()
				.filter(g -> g.getSlug().equals(slug) && !g.getId().equals(id))
				.findAny()
				.ifPresent(g -> {
					throw new IllegalArgumentException("Ce slug de genre existe deja.");
				});
		genreRepository.update(id, slug, form.getLabel());
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		getById(id);
		long linked = genreRepository.countGamesByGenreId(id);
		if (linked > 0) {
			throw new IllegalStateException("Genre utilise par " + linked + " jeu(x).");
		}
		genreRepository.deleteById(id);
	}
}
