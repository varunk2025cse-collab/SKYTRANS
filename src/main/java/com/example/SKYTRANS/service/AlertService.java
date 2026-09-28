package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Alert;
import com.example.SKYTRANS.repository.AlertRepository;

@Service
public class AlertService {
	private final AlertRepository alertRepository;

	public AlertService(AlertRepository alertRepository) { this.alertRepository = alertRepository; }

	public List<Alert> findAll() { return alertRepository.findAll(); }
	public List<Alert> findOpen() { return alertRepository.findByResolvedFalseOrderByCreatedAtDesc(); }

	public Alert resolve(Long id) {
		Alert alert = alertRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + id));
		alert.setResolved(true);
		return alertRepository.save(alert);
	}
}