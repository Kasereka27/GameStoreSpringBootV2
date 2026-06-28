package com.examen.gamestore.exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException() {
		super("Utilisateur introuvable.");
	}
}
