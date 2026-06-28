package com.examen.gamestore.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CheckoutForm {

	@NotBlank(message = "Le prénom est obligatoire")
	@Size(max = 100)
	private String firstName;

	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 100)
	private String lastName;

	@NotBlank(message = "L'e-mail est obligatoire")
	@Email(message = "E-mail invalide")
	private String email;

	@Size(max = 50)
	private String phone;

	@NotBlank(message = "L'adresse est obligatoire")
	@Size(max = 255)
	private String address;

	@NotBlank(message = "Le code postal est obligatoire")
	@Size(max = 20)
	private String postalCode;

	@NotBlank(message = "La ville est obligatoire")
	@Size(max = 100)
	private String city;

	@NotBlank(message = "Le pays est obligatoire")
	@Size(max = 2)
	private String country = "FR";

	@NotBlank(message = "Choisissez un mode de paiement")
	private String paymentMethod = "card";

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
