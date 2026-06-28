package com.examen.gamestore.exception;

public class EmptyCartException extends RuntimeException {

	public EmptyCartException() {
		super("Votre panier est vide.");
	}
}
