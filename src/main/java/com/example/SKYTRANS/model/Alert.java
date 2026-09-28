package com.example.SKYTRANS.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerts")
public class Alert {
	public enum AlertType { HIGH_CABLE_TENSION, LOW_CABLE_TENSION, HIGH_MOTOR_POWER, OVERSPEED, OVER_CAPACITY }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "cable_car_id")
	private CableCar cableCar;

	@Enumerated(EnumType.STRING)
	private AlertType type;

	private String message;
	private String severity;
	private LocalDateTime createdAt;
	private boolean resolved;

	public Alert() { }

	@PrePersist
	public void setCreatedAtIfMissing() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public CableCar getCableCar() { return cableCar; }
	public void setCableCar(CableCar cableCar) { this.cableCar = cableCar; }
	public AlertType getType() { return type; }
	public void setType(AlertType type) { this.type = type; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public String getSeverity() { return severity; }
	public void setSeverity(String severity) { this.severity = severity; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public boolean isResolved() { return resolved; }
	public void setResolved(boolean resolved) { this.resolved = resolved; }
}