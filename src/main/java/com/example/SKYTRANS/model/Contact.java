package com.example.SKYTRANS.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "contacts")
public class Contact {
	public enum ContactType { CUSTOMER, VENDOR }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	@Enumerated(EnumType.STRING)
	private ContactType type;

	@Email
	private String email;

	private String phone;
	private String address;

	public Contact() { }

	public Contact(String name, ContactType type, String email, String phone, String address) {
		this.name = name;
		this.type = type;
		this.email = email;
		this.phone = phone;
		this.address = address;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public ContactType getType() { return type; }
	public void setType(ContactType type) { this.type = type; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
}