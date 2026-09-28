package com.example.SKYTRANS.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "safety_configurations")
// Safety limits are stored in the database (one record). Initial demo values are created by SampleDataLoader.
public class SafetyConfiguration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Positive
	private BigDecimal maximumCableSpeed;

	@NotNull
	@Positive
	private BigDecimal minimumCableTension;

	@NotNull
	@Positive
	private BigDecimal maximumCableTension;

	@NotNull
	@Positive
	private BigDecimal maximumMotorPower;

	@Positive
	private int maximumPassengerCapacity;

	public SafetyConfiguration() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public BigDecimal getMaximumCableSpeed() { return maximumCableSpeed; }
	public void setMaximumCableSpeed(BigDecimal maximumCableSpeed) { this.maximumCableSpeed = maximumCableSpeed; }
	public BigDecimal getMinimumCableTension() { return minimumCableTension; }
	public void setMinimumCableTension(BigDecimal minimumCableTension) { this.minimumCableTension = minimumCableTension; }
	public BigDecimal getMaximumCableTension() { return maximumCableTension; }
	public void setMaximumCableTension(BigDecimal maximumCableTension) { this.maximumCableTension = maximumCableTension; }
	public BigDecimal getMaximumMotorPower() { return maximumMotorPower; }
	public void setMaximumMotorPower(BigDecimal maximumMotorPower) { this.maximumMotorPower = maximumMotorPower; }
	public int getMaximumPassengerCapacity() { return maximumPassengerCapacity; }
	public void setMaximumPassengerCapacity(int maximumPassengerCapacity) { this.maximumPassengerCapacity = maximumPassengerCapacity; }
}