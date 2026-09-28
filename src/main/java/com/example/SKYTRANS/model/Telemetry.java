package com.example.SKYTRANS.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "cable_car_telemetry")
public class Telemetry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "cable_car_id")
	private CableCar cableCar;

	@Min(0)
	private int passengerCount;

	@NotNull
	@PositiveOrZero
	private BigDecimal cableTension;

	@NotNull
	@PositiveOrZero
	private BigDecimal motorPowerUsage;

	@NotNull
	@PositiveOrZero
	private BigDecimal speed;

	@NotNull
	@DecimalMin("0.0")
	@DecimalMax("100.0")
	private BigDecimal batteryLevel;

	@NotNull
	private BigDecimal temperature;

	private LocalDateTime timestamp;

	public Telemetry() { }

	@PrePersist
	public void setTimestampIfMissing() {
		if (timestamp == null) {
			timestamp = LocalDateTime.now();
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public CableCar getCableCar() { return cableCar; }
	public void setCableCar(CableCar cableCar) { this.cableCar = cableCar; }
	public int getPassengerCount() { return passengerCount; }
	public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }
	public BigDecimal getCableTension() { return cableTension; }
	public void setCableTension(BigDecimal cableTension) { this.cableTension = cableTension; }
	public BigDecimal getMotorPowerUsage() { return motorPowerUsage; }
	public void setMotorPowerUsage(BigDecimal motorPowerUsage) { this.motorPowerUsage = motorPowerUsage; }
	public BigDecimal getSpeed() { return speed; }
	public void setSpeed(BigDecimal speed) { this.speed = speed; }
	public BigDecimal getBatteryLevel() { return batteryLevel; }
	public void setBatteryLevel(BigDecimal batteryLevel) { this.batteryLevel = batteryLevel; }
	public BigDecimal getTemperature() { return temperature; }
	public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
	public LocalDateTime getTimestamp() { return timestamp; }
	public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}