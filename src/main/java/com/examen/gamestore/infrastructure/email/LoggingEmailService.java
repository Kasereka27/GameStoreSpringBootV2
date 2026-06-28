package com.examen.gamestore.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "test", "default"})
public class LoggingEmailService implements EmailService {

	private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

	@Override
	public void sendPasswordResetEmail(String to, String resetUrl) {
		log.info("[EMAIL] Réinitialisation mot de passe pour {} — lien : {}", to, resetUrl);
	}

	@Override
	public void sendWelcomeEmail(String to, String firstName) {
		log.info("[EMAIL] Bienvenue {} ({})", firstName, to);
	}

	@Override
	public void sendOrderConfirmation(String to, String firstName, String orderNumber,
			java.math.BigDecimal totalAmount) {
		log.info("[EMAIL] Commande {} confirmée pour {} ({}) — total {} €",
				orderNumber, firstName, to, totalAmount);
	}
}
