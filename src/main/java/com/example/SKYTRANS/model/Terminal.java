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
@Table(name = "terminals")
public class Terminal {
	public enum Status { ACTIVE, INACTIVE, MAINTENANCE }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String terminalCode;

	@NotBlank
	private String name;

	@NotBlank
	private String location;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

	public Terminal() { }

	public Terminal(String terminalCode, String name, String location) {
		this.terminalCode = terminalCode;
		this.name = name;
		this.location = location;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getTerminalCode() { return terminalCode; }
	public void setTerminalCode(String terminalCode) { this.terminalCode = terminalCode; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
}