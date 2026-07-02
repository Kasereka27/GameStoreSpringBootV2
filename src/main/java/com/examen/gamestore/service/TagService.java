package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.Tag;
import com.examen.gamestore.web.dto.TagForm;

public interface TagService {

	List<Tag> findAll();

	Tag getById(UUID id);

	UUID create(TagForm form);

	void update(UUID id, TagForm form);

	void delete(UUID id);
}
