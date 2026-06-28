package com.examen.gamestore.exception;

public class GameNotFoundException extends RuntimeException {

	public GameNotFoundException(String slug) {
		super("Jeu introuvable : " + slug);
	}
}
