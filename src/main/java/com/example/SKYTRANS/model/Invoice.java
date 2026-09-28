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
@Table(name = "customer_invoices")
public class Invoice {
	public enum Status { UNPAID, PAID }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "sales_order_id", unique = true)
	private SalesOrder salesOrder;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Contact customer;

	private int passengerTrips;

	@Positive
	private BigDecimal amount;

	private LocalDate invoiceDate;

	@Enumerated(EnumType.STRING)
	private Status status = Status.UNPAID;

	public Invoice() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public SalesOrder getSalesOrder() { return salesOrder; }
	public void setSalesOrder(SalesOrder salesOrder) { this.salesOrder = salesOrder; }
	public Contact getCustomer() { return customer; }
	public void setCustomer(Contact customer) { this.customer = customer; }
	public int getPassengerTrips() { return passengerTrips; }
	public void setPassengerTrips(int passengerTrips) { this.passengerTrips = passengerTrips; }
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	public LocalDate getInvoiceDate() { return invoiceDate; }
	public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
}