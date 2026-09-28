package com.example.SKYTRANS.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "journals")
public class Journal {
	public enum JournalType { SALES, PURCHASE, CASH, BANK }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	private JournalType type;

	public Journal() { }

	public Journal(String name, JournalType type) {
		this.name = name;
		this.type = type;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public JournalType getType() { return type; }
	public void setType(JournalType type) { this.type = type; }
}