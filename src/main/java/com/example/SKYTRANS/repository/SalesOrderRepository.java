package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.SalesOrder;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> { }