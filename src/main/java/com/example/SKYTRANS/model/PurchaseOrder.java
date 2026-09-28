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
@Table(name = "purchase_orders")
public class PurchaseOrder {
	public enum Status { CREATED, APPROVED, BILLED, CANCELLED }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Contact vendor;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Min(1)
	private int quantity;

	@Positive
	private BigDecimal unitPrice;

	@Positive
	private BigDecimal totalAmount;

	private LocalDate orderDate;

	@Enumerated(EnumType.STRING)
	private Status status = Status.CREATED;

	public PurchaseOrder() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Contact getVendor() { return vendor; }
	public void setVendor(Contact vendor) { this.vendor = vendor; }
	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
	public BigDecimal getTotalAmount() { return totalAmount; }
	public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
	public LocalDate getOrderDate() { return orderDate; }
	public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
}