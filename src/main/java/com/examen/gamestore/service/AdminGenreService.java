package com.examen.gamestore.service;

import java.util.List;
import java.util.UUID;

import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.web.dto.request.GenreForm;

public interface AdminGenreService {

	List<Genre> findAll();

	Genre getById(UUID id);

	UUID create(GenreForm form);

	void update(UUID id, GenreForm form);

	void delete(UUID id);
}
