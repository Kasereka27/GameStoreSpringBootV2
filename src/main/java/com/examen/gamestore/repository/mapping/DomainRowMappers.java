package com.examen.gamestore.repository.mapping;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.Genre;
import com.examen.gamestore.domain.model.Tag;

@Component
public class DomainRowMappers {

	private final RowMapper<Genre> genre;
	private final RowMapper<Tag> tag;
	private final RowMapper<Game> game;

	public DomainRowMappers(JdbcTemplateMapperFactory mapperFactory) {
		this.genre = mapperFactory.newRowMapper(Genre.class);
		this.tag = mapperFactory.newRowMapper(Tag.class);
		this.game = mapperFactory.newRowMapper(Game.class);
	}

	public RowMapper<Genre> genre() {
		return genre;
	}

	public RowMapper<Tag> tag() {
		return tag;
	}

	public RowMapper<Game> game() {
		return game;
	}
}
