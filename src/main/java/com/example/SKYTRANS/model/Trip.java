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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trips")
public class Trip {
	public enum Status { SCHEDULED, RUNNING, COMPLETED, CANCELLED }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "cable_car_id")
	private CableCar cableCar;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "route_id")
	private TransitRoute route;

	@Min(0)
	private int passengerCount;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status = Status.SCHEDULED;

	public Trip() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public CableCar getCableCar() { return cableCar; }
	public void setCableCar(CableCar cableCar) { this.cableCar = cableCar; }
	public TransitRoute getRoute() { return route; }
	public void setRoute(TransitRoute route) { this.route = route; }
	public int getPassengerCount() { return passengerCount; }
	public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }
	public LocalDateTime getStartTime() { return startTime; }
	public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
	public LocalDateTime getEndTime() { return endTime; }
	public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
}