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
@Table(name = "transit_routes")
public class TransitRoute {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String routeCode;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "source_terminal_id")
	private Terminal sourceTerminal;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "destination_terminal_id")
	private Terminal destinationTerminal;

	@NotNull
	@Positive
	private BigDecimal distance;

	@Positive
	private int estimatedTime;

	@NotBlank
	private String status = "ACTIVE";

	public TransitRoute() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getRouteCode() { return routeCode; }
	public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
	public Terminal getSourceTerminal() { return sourceTerminal; }
	public void setSourceTerminal(Terminal sourceTerminal) { this.sourceTerminal = sourceTerminal; }
	public Terminal getDestinationTerminal() { return destinationTerminal; }
	public void setDestinationTerminal(Terminal destinationTerminal) { this.destinationTerminal = destinationTerminal; }
	public BigDecimal getDistance() { return distance; }
	public void setDistance(BigDecimal distance) { this.distance = distance; }
	public int getEstimatedTime() { return estimatedTime; }
	public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}