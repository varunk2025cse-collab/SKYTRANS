package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.VendorBill;

public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
	boolean existsByPurchaseOrderId(Long purchaseOrderId);
}