package com.example.SKYTRANS.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "sales_orders")
public class SalesOrder {
	public enum Status { CREATED, INVOICED, CANCELLED }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Contact customer;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Min(0)
	private int passengerTrips;

	@Positive
	private BigDecimal ratePerPassenger;

	@Positive
	private BigDecimal totalAmount;

	private LocalDate orderDate;

	@Enumerated(EnumType.STRING)
	private Status status = Status.CREATED;

	public SalesOrder() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Contact getCustomer() { return customer; }
	public void setCustomer(Contact customer) { this.customer = customer; }
	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }
	public int getPassengerTrips() { return passengerTrips; }
	public void setPassengerTrips(int passengerTrips) { this.passengerTrips = passengerTrips; }
	public BigDecimal getRatePerPassenger() { return ratePerPassenger; }
	public void setRatePerPassenger(BigDecimal ratePerPassenger) { this.ratePerPassenger = ratePerPassenger; }
	public BigDecimal getTotalAmount() { return totalAmount; }
	public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
	public LocalDate getOrderDate() { return orderDate; }
	public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
}