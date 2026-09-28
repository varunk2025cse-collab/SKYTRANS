package com.example.SKYTRANS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Alert;
import com.example.SKYTRANS.service.AlertService;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
	private final AlertService alertService;

	public AlertController(AlertService alertService) { this.alertService = alertService; }

	@GetMapping
	public List<Alert> findOpen() { return alertService.findOpen(); }

	@GetMapping("/all")
	public List<Alert> findAll() { return alertService.findAll(); }

	@PostMapping("/{id}/resolve")
	public Alert resolve(@PathVariable Long id) { return alertService.resolve(id); }
}