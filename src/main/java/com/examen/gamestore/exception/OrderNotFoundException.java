package com.examen.gamestore.exception;

public class OrderNotFoundException extends RuntimeException {

	public OrderNotFoundException(String identifier) {
		super("Commande introuvable : " + identifier);
	}
}
