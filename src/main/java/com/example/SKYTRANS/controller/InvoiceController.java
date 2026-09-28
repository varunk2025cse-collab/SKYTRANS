package com.example.SKYTRANS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.service.InvoiceService;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
	private final InvoiceService invoiceService;

	public InvoiceController(InvoiceService invoiceService) { this.invoiceService = invoiceService; }

	@GetMapping
	public List<Invoice> findAll() { return invoiceService.findAll(); }

	// No @Valid here: the request only carries the sales order id. The service looks up the order
	// and fills in the customer, passenger trips and amount before saving.
	@PostMapping
	public Invoice create(@RequestBody Invoice invoice) { return invoiceService.create(invoice); }
}