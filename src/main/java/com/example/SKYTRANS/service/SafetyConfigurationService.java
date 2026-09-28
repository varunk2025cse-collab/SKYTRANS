package com.example.SKYTRANS.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.SafetyConfiguration;
import com.example.SKYTRANS.repository.SafetyConfigurationRepository;

// All safety limits come from the SafetyConfiguration record in the database.
// Other services ask this class for limits instead of using their own numbers.
@Service
public class SafetyConfigurationService {
	private final SafetyConfigurationRepository configurationRepository;

	public SafetyConfigurationService(SafetyConfigurationRepository configurationRepository) {
		this.configurationRepository = configurationRepository;
	}

	public SafetyConfiguration getCurrent() {
		return configurationRepository.findFirstByOrderByIdAsc()
				.orElseThrow(() -> new BusinessException("Safety configuration has not been set up yet"));
	}

	public SafetyConfiguration update(SafetyConfiguration configuration) {
		if (configuration.getMinimumCableTension().compareTo(configuration.getMaximumCableTension()) >= 0) {
			throw new BusinessException("Minimum cable tension must be below maximum cable tension");
		}
		// Keep a single record: overwrite the existing one if it is there
		configurationRepository.findFirstByOrderByIdAsc()
				.ifPresent(existing -> configuration.setId(existing.getId()));
		return configurationRepository.save(configuration);
	}

	// A car may not carry more than its own capacity or the configured system maximum
	public int getAllowedPassengers(CableCar cableCar) {
		return Math.min(cableCar.getCapacity(), getCurrent().getMaximumPassengerCapacity());
	}

	// A car may not go faster than its own maximum or the configured cable speed maximum
	public BigDecimal getAllowedSpeed(CableCar cableCar) {
		return getCurrent().getMaximumCableSpeed().min(cableCar.getMaximumSpeed());
	}
}
