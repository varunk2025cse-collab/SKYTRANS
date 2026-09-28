package com.example.SKYTRANS.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.SKYTRANS.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	@Query("select sum(p.amount) from Payment p where p.invoice.id = :invoiceId")
	BigDecimal sumByInvoiceId(@Param("invoiceId") Long invoiceId);

	@Query("select sum(p.amount) from Payment p where p.vendorBill.id = :billId")
	BigDecimal sumByVendorBillId(@Param("billId") Long billId);
}