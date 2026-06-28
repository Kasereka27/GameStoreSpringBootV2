package com.examen.gamestore.exception;

public class InvalidTokenException extends RuntimeException {

	public InvalidTokenException() {
		super("Lien invalide ou expiré.");
	}
}
