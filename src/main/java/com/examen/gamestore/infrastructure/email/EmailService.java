package com.examen.gamestore.infrastructure.email;

public interface EmailService {

	void sendPasswordResetEmail(String to, String resetUrl);

	void sendWelcomeEmail(String to, String firstName);

	void sendOrderConfirmation(String to, String firstName, String orderNumber, java.math.BigDecimal totalAmount);
}
