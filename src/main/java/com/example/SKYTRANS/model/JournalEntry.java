package com.example.SKYTRANS.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private LocalDate date;

	private String description;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "journal_id")
	private Journal journal;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "debit_account_id")
	private Account debitAccount;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "credit_account_id")
	private Account creditAccount;

	@NotNull
	@Positive
	private BigDecimal amount;

	public JournalEntry() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public LocalDate getDate() { return date; }
	public void setDate(LocalDate date) { this.date = date; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public Journal getJournal() { return journal; }
	public void setJournal(Journal journal) { this.journal = journal; }
	public Account getDebitAccount() { return debitAccount; }
	public void setDebitAccount(Account debitAccount) { this.debitAccount = debitAccount; }
	public Account getCreditAccount() { return creditAccount; }
	public void setCreditAccount(Account creditAccount) { this.creditAccount = creditAccount; }
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
}