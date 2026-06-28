package com.examen.gamestore.exception;

public class InsufficientStockException extends RuntimeException {

	public InsufficientStockException(String gameTitle) {
		super("Stock insuffisant pour : " + gameTitle);
	}
}
