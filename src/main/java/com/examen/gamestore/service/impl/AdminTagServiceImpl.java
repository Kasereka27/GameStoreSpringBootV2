package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.repository.TagRepository;
import com.examen.gamestore.service.AdminTagService;
import com.examen.gamestore.util.SlugUtils;
import com.examen.gamestore.web.dto.request.TagForm;

@Service
public class AdminTagServiceImpl implements AdminTagService {

	private final TagRepository tagRepository;

	public AdminTagServiceImpl(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Override
	public List<Tag> findAll() {
		return tagRepository.findAll();
	}

	@Override
	public Tag getById(UUID id) {
		return tagRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Tag introuvable."));
	}

	@Override
	@Transactional
	public UUID create(TagForm form) {
		String slug = resolveSlug(form);
		if (tagRepository.existsBySlug(slug)) {
			throw new IllegalArgumentException("Ce slug de tag existe déjà.");
		}
		return tagRepository.insert(slug, form.getLabel());
	}

	@Override
	@Transactional
	public void update(UUID id, TagForm form) {
		getById(id);
		String slug = resolveSlug(form);
		tagRepository.findBySlug(slug).ifPresent(existing -> {
			if (!existing.getId().equals(id)) {
				throw new IllegalArgumentException("Ce slug de tag existe déjà.");
			}
		});
		tagRepository.update(id, slug, form.getLabel());
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		getById(id);
		long linked = tagRepository.countGamesByTagId(id);
		if (linked > 0) {
			throw new IllegalStateException("Ce tag est utilisé par " + linked + " jeu(x). Retirez-le d'abord.");
		}
		tagRepository.deleteById(id);
	}

	private String resolveSlug(TagForm form) {
		if (form.getSlug() != null && !form.getSlug().isBlank()) {
			return form.getSlug();
		}
		return SlugUtils.toSlug(form.getLabel());
	}
}
