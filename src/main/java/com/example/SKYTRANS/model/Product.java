package com.example.SKYTRANS.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	private String description;

	@NotBlank
	private String type;

	@NotNull
	@Positive
	private BigDecimal unitPrice;

	private boolean active = true;

	// Ledger account used when this product is sold (an INCOME account)
	// or bought (an EXPENSE or ASSET account). This decides the accounting, not the product name.
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	public Product() { }

	public Product(String name, String description, String type, BigDecimal unitPrice) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.unitPrice = unitPrice;
		this.active = true;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public Account getAccount() { return account; }
	public void setAccount(Account account) { this.account = account; }
}