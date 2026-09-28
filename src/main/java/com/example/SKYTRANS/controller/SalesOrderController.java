package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.SalesOrder;
import com.example.SKYTRANS.service.SalesOrderService;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {
	private final SalesOrderService salesOrderService;

	public SalesOrderController(SalesOrderService salesOrderService) { this.salesOrderService = salesOrderService; }

	@GetMapping
	public List<SalesOrder> findAll() { return salesOrderService.findAll(); }

	@PostMapping
	public SalesOrder create(@Valid @RequestBody SalesOrder salesOrder) {
		return salesOrderService.create(salesOrder);
	}
}