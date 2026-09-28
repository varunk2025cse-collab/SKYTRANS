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
@Table(name = "payments")
public class Payment {
	public enum Type { CUSTOMER, VENDOR }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Type type;

	@ManyToOne
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;

	@ManyToOne
	@JoinColumn(name = "vendor_bill_id")
	private VendorBill vendorBill;

	@NotNull
	@Positive
	private BigDecimal amount;

	private LocalDate paymentDate;

	public Payment() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Type getType() { return type; }
	public void setType(Type type) { this.type = type; }
	public Invoice getInvoice() { return invoice; }
	public void setInvoice(Invoice invoice) { this.invoice = invoice; }
	public VendorBill getVendorBill() { return vendorBill; }
	public void setVendorBill(VendorBill vendorBill) { this.vendorBill = vendorBill; }
	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
	public LocalDate getPaymentDate() { return paymentDate; }
	public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}