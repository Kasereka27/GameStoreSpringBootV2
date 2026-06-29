package com.examen.gamestore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleGameNotFound(GameNotFoundException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "404";
	}

	@ExceptionHandler(OrderNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleOrderNotFound(OrderNotFoundException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "404";
	}
}
