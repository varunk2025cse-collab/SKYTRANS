package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Telemetry;
import com.example.SKYTRANS.service.TelemetryService;

@RestController
@RequestMapping("/api/cablecar")
public class TelemetryController {
	private final TelemetryService telemetryService;

	public TelemetryController(TelemetryService telemetryService) { this.telemetryService = telemetryService; }

	@PostMapping("/telemetry")
	public Telemetry receive(@Valid @RequestBody Telemetry telemetry) {
		return telemetryService.receive(telemetry);
	}

	@GetMapping("/telemetry")
	public List<Telemetry> findAll() { return telemetryService.findAll(); }

	@GetMapping("/{id}/telemetry")
	public List<Telemetry> findForCableCar(@PathVariable Long id) {
		return telemetryService.findForCableCar(id);
	}
}