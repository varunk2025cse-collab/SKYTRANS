package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Payment;
import com.example.SKYTRANS.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

	@GetMapping
	public List<Payment> findAll() { return paymentService.findAll(); }

	@PostMapping("/customer")
	public Payment receiveCustomerPayment(@Valid @RequestBody Payment payment) {
		return paymentService.receiveCustomerPayment(payment);
	}

	@PostMapping("/vendor")
	public Payment payVendor(@Valid @RequestBody Payment payment) { return paymentService.payVendor(payment); }
}