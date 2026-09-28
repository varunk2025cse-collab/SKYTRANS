package com.example.SKYTRANS.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.SafetyConfiguration;
import com.example.SKYTRANS.service.SafetyConfigurationService;

@RestController
@RequestMapping("/api/safety-configuration")
public class SafetyConfigurationController {
	private final SafetyConfigurationService configurationService;

	public SafetyConfigurationController(SafetyConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@GetMapping
	public SafetyConfiguration getCurrent() { return configurationService.getCurrent(); }

	@PutMapping
	public SafetyConfiguration update(@Valid @RequestBody SafetyConfiguration configuration) {
		return configurationService.update(configuration);
	}
}