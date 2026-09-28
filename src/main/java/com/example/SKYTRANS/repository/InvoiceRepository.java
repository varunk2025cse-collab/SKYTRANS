package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	boolean existsBySalesOrderId(Long salesOrderId);
}