package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> { }