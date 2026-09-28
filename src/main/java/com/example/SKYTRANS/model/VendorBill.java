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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "vendor_bills")
public class VendorBill {
	public enum Status { UNPAID, PAID }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "purchase_order_id", unique = true)
	private PurchaseOrder purchaseOrder;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Contact vendor;

	@Positive
	private BigDecimal amount;

	private LocalDate billDate;

	@Enumerated(EnumType.STRING)
	private Status status = Status.UNPAID;

	public VendorBill() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
	public Contact getVendor() { return vendor; }
	public void setVendor(Contact vendor) { this.vendor = vendor; }
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	public LocalDate getBillDate() { return billDate; }
	public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }
}