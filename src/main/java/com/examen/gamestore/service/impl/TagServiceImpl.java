package com.examen.gamestore.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.repository.TagRepository;
import com.examen.gamestore.service.TagService;
import com.examen.gamestore.web.dto.TagForm;
import com.examen.gamestore.web.mapper.TagMapper;

@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;
	private final TagMapper tagMapper;

	public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
		this.tagRepository = tagRepository;
		this.tagMapper = tagMapper;
	}

	@Override
	public List<Tag> findAll() {
		return tagRepository.findAll();
	}

	@Override
	public Tag getById(UUID id) {
		return tagRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Tag introuvable : " + id));
	}

	@Override
	@Transactional
	public UUID create(TagForm form) {
		String slug = tagMapper.resolveSlug(form);
		if (tagRepository.existsBySlug(slug)) {
			throw new IllegalArgumentException("Ce slug de tag existe deja.");
		}
		return tagRepository.insert(slug, form.getLabel());
	}

	@Override
	@Transactional
	public void update(UUID id, TagForm form) {
		getById(id);
		String slug = tagMapper.resolveSlug(form);
		tagRepository.findAll().stream()
				.filter(t -> t.getSlug().equals(slug) && !t.getId().equals(id))
				.findAny()
				.ifPresent(t -> {
					throw new IllegalArgumentException("Ce slug de tag existe deja.");
				});
		tagRepository.update(id, slug, form.getLabel());
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		getById(id);
		long linked = tagRepository.countGamesByTagId(id);
		if (linked > 0) {
			throw new IllegalStateException("Tag utilise par " + linked + " jeu(x).");
		}
		tagRepository.deleteById(id);
	}
}
