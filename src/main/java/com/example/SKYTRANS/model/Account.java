package com.example.SKYTRANS.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "accounts")
public class Account {
	public enum AccountType { ASSET, LIABILITY, INCOME, EXPENSE }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(unique = true)
	private String accountCode;

	@NotBlank
	@Column(unique = true)
	private String accountName;

	@NotNull
	@Enumerated(EnumType.STRING)
	private AccountType accountType;

	@NotNull
	private BigDecimal openingBalance = BigDecimal.ZERO;

	private boolean active = true;

	public Account() { }

	public Account(String accountCode, String accountName, AccountType accountType, BigDecimal openingBalance) {
		this.accountCode = accountCode;
		this.accountName = accountName;
		this.accountType = accountType;
		this.openingBalance = openingBalance == null ? BigDecimal.ZERO : openingBalance;
		this.active = true;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getAccountCode() { return accountCode; }
	public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
	public String getAccountName() { return accountName; }
	public void setAccountName(String accountName) { this.accountName = accountName; }
	public AccountType getAccountType() { return accountType; }
	public void setAccountType(AccountType accountType) { this.accountType = accountType; }
	public BigDecimal getOpeningBalance() { return openingBalance; }
	public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
}