package com.examen.gamestore.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GameNotFoundException.class)
	public String handleGameNotFound(GameNotFoundException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "404";
	}
}
