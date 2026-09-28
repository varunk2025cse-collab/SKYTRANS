package com.example.SKYTRANS.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Alert;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.SafetyConfiguration;
import com.example.SKYTRANS.model.Telemetry;
import com.example.SKYTRANS.repository.AlertRepository;
import com.example.SKYTRANS.repository.CableCarRepository;
import com.example.SKYTRANS.repository.TelemetryRepository;

@Service
public class TelemetryService {
	private final TelemetryRepository telemetryRepository;
	private final CableCarRepository cableCarRepository;
	private final AlertRepository alertRepository;
	private final SafetyConfigurationService configurationService;

	public TelemetryService(TelemetryRepository telemetryRepository, CableCarRepository cableCarRepository,
			AlertRepository alertRepository, SafetyConfigurationService configurationService) {
		this.telemetryRepository = telemetryRepository;
		this.cableCarRepository = cableCarRepository;
		this.alertRepository = alertRepository;
		this.configurationService = configurationService;
	}

	@Transactional
	public Telemetry receive(Telemetry telemetry) {
		Long cableCarId = telemetry.getCableCar() == null ? null : telemetry.getCableCar().getId();
		CableCar cableCar = cableCarId == null ? null : cableCarRepository.findById(cableCarId).orElse(null);
		if (cableCar == null) {
			throw new ResourceNotFoundException("Cable car not found");
		}

		SafetyConfiguration limits = configurationService.getCurrent();
		checkTension(telemetry, cableCar, limits);
		checkMotorPower(telemetry, cableCar, limits);
		checkSpeed(telemetry, cableCar);
		checkPassengerCount(telemetry, cableCar);

		cableCar.setCurrentSpeed(telemetry.getSpeed());
		cableCar.setMotorPower(telemetry.getMotorPowerUsage());
		cableCar.setBatteryLevel(telemetry.getBatteryLevel());
		cableCarRepository.save(cableCar);
		telemetry.setCableCar(cableCar);
		return telemetryRepository.save(telemetry);
	}

	public List<Telemetry> findAll() { return telemetryRepository.findAll(); }
	public List<Telemetry> findForCableCar(Long id) { return telemetryRepository.findByCableCarIdOrderByTimestampDesc(id); }

	private void checkTension(Telemetry telemetry, CableCar cableCar, SafetyConfiguration limits) {
		if (telemetry.getCableTension().compareTo(limits.getMinimumCableTension()) < 0) {
			createAlert(cableCar, Alert.AlertType.LOW_CABLE_TENSION, "Cable tension is below the configured minimum", "CRITICAL");
		}
		if (telemetry.getCableTension().compareTo(limits.getMaximumCableTension()) > 0) {
			createAlert(cableCar, Alert.AlertType.HIGH_CABLE_TENSION, "Cable tension is above the configured maximum", "CRITICAL");
		}
	}

	private void checkMotorPower(Telemetry telemetry, CableCar cableCar, SafetyConfiguration limits) {
		if (telemetry.getMotorPowerUsage().compareTo(limits.getMaximumMotorPower()) > 0) {
			createAlert(cableCar, Alert.AlertType.HIGH_MOTOR_POWER, "Motor power is above the configured maximum", "WARNING");
		}
	}

	private void checkSpeed(Telemetry telemetry, CableCar cableCar) {
		BigDecimal maximumSpeed = configurationService.getAllowedSpeed(cableCar);
		if (telemetry.getSpeed().compareTo(maximumSpeed) > 0) {
			createAlert(cableCar, Alert.AlertType.OVERSPEED, "Cable car speed is above the allowed maximum", "CRITICAL");
		}
	}

	private void checkPassengerCount(Telemetry telemetry, CableCar cableCar) {
		int allowedPassengers = configurationService.getAllowedPassengers(cableCar);
		if (telemetry.getPassengerCount() > allowedPassengers) {
			createAlert(cableCar, Alert.AlertType.OVER_CAPACITY, "Passenger count is above the allowed capacity", "CRITICAL");
		}
	}

	private void createAlert(CableCar cableCar, Alert.AlertType type, String message, String severity) {
		Alert alert = new Alert();
		alert.setCableCar(cableCar);
		alert.setType(type);
		alert.setMessage(message);
		alert.setSeverity(severity);
		alertRepository.save(alert);
	}
}