package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.PurchaseOrder;
import com.example.SKYTRANS.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {
	private final PurchaseOrderService purchaseOrderService;

	public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
		this.purchaseOrderService = purchaseOrderService;
	}

	@GetMapping
	public List<PurchaseOrder> findAll() { return purchaseOrderService.findAll(); }

	@PostMapping
	public PurchaseOrder create(@Valid @RequestBody PurchaseOrder purchaseOrder) {
		return purchaseOrderService.create(purchaseOrder);
	}

	@PostMapping("/{id}/approve")
	public PurchaseOrder approve(@PathVariable Long id) { return purchaseOrderService.approve(id); }

	@PostMapping("/{id}/cancel")
	public PurchaseOrder cancel(@PathVariable Long id) { return purchaseOrderService.cancel(id); }
}