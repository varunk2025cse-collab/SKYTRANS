package com.example.SKYTRANS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.service.VendorBillService;

@RestController
@RequestMapping("/api/vendor-bills")
public class VendorBillController {
	private final VendorBillService vendorBillService;

	public VendorBillController(VendorBillService vendorBillService) { this.vendorBillService = vendorBillService; }

	@GetMapping
	public List<VendorBill> findAll() { return vendorBillService.findAll(); }

	// No @Valid here: the request only carries the purchase order id. The service looks up the order
	// and fills in the vendor and amount before saving.
	@PostMapping
	public VendorBill create(@RequestBody VendorBill request) {
		return vendorBillService.create(request);
	}
}