package com.example.SKYTRANS.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "cable_cars")
public class CableCar {
	public enum Status { READY, RUNNING, STOPPED, MAINTENANCE, EMERGENCY }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String vehicleNumber;

	@Min(1)
	private int capacity;

	@NotNull
	@Positive
	private BigDecimal maximumSpeed;

	@NotNull
	@PositiveOrZero
	private BigDecimal currentSpeed = BigDecimal.ZERO;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status = Status.READY;

	@NotNull
	@DecimalMin("0.0")
	@DecimalMax("100.0")
	private BigDecimal batteryLevel = BigDecimal.ZERO;

	@NotNull
	@PositiveOrZero
	private BigDecimal motorPower = BigDecimal.ZERO;

	// Tells Jackson to build CableCar with this empty constructor and then call the setters.
	// Without it, a request such as {"cableCar": {"id": 1}} fails because "capacity" is missing.
	@JsonCreator
	public CableCar() { }

	public CableCar(String vehicleNumber, int capacity, BigDecimal maximumSpeed) {
		this.vehicleNumber = vehicleNumber;
		this.capacity = capacity;
		this.maximumSpeed = maximumSpeed;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getVehicleNumber() { return vehicleNumber; }
	public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
	public int getCapacity() { return capacity; }
	public void setCapacity(int capacity) { this.capacity = capacity; }
	public BigDecimal getMaximumSpeed() { return maximumSpeed; }
	public void setMaximumSpeed(BigDecimal maximumSpeed) { this.maximumSpeed = maximumSpeed; }
	public BigDecimal getCurrentSpeed() { return currentSpeed; }
	public void setCurrentSpeed(BigDecimal currentSpeed) { this.currentSpeed = currentSpeed; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
	public BigDecimal getBatteryLevel() { return batteryLevel; }
	public void setBatteryLevel(BigDecimal batteryLevel) { this.batteryLevel = batteryLevel; }
	public BigDecimal getMotorPower() { return motorPower; }
	public void setMotorPower(BigDecimal motorPower) { this.motorPower = motorPower; }
}