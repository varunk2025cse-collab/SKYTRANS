package com.example.SKYTRANS.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.TransitRoute;
import com.example.SKYTRANS.model.Trip;
import com.example.SKYTRANS.repository.CableCarRepository;
import com.example.SKYTRANS.repository.TransitRouteRepository;
import com.example.SKYTRANS.repository.TripRepository;

@Service
public class TripService {
	private final TripRepository tripRepository;
	private final CableCarRepository cableCarRepository;
	private final TransitRouteRepository routeRepository;
	private final SafetyConfigurationService configurationService;

	public TripService(TripRepository tripRepository, CableCarRepository cableCarRepository,
			TransitRouteRepository routeRepository, SafetyConfigurationService configurationService) {
		this.tripRepository = tripRepository;
		this.cableCarRepository = cableCarRepository;
		this.routeRepository = routeRepository;
		this.configurationService = configurationService;
	}

	public List<Trip> findAll() { return tripRepository.findAll(); }

	public Trip create(Trip trip) {
		if (trip.getCableCar() == null || trip.getCableCar().getId() == null || trip.getRoute() == null
				|| trip.getRoute().getId() == null) {
			throw new BusinessException("A cable car and route are required");
		}
		CableCar cableCar = cableCarRepository.findById(trip.getCableCar().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Cable car not found"));
		TransitRoute route = routeRepository.findById(trip.getRoute().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Route not found"));
		int allowedPassengers = configurationService.getAllowedPassengers(cableCar);
		if (trip.getPassengerCount() < 0 || trip.getPassengerCount() > allowedPassengers) {
			throw new BusinessException("Passenger count must be between 0 and " + allowedPassengers + " for this cable car");
		}
		trip.setCableCar(cableCar);
		trip.setRoute(route);
		trip.setStatus(Trip.Status.SCHEDULED);
		return tripRepository.save(trip);
	}

	@Transactional
	public Trip start(Long id) {
		Trip trip = findById(id);
		if (trip.getStatus() != Trip.Status.SCHEDULED) {
			throw new BusinessException("Only a scheduled trip can be started");
		}
		CableCar cableCar = trip.getCableCar();
		if (cableCar.getStatus() == CableCar.Status.MAINTENANCE
				|| cableCar.getStatus() == CableCar.Status.EMERGENCY
				|| cableCar.getStatus() == CableCar.Status.RUNNING) {
			throw new BusinessException("Cable car is not available to start a trip");
		}
		cableCar.setStatus(CableCar.Status.RUNNING);
		trip.setStatus(Trip.Status.RUNNING);
		trip.setStartTime(LocalDateTime.now());
		return tripRepository.save(trip);
	}

	@Transactional
	public Trip complete(Long id) {
		Trip trip = findById(id);
		if (trip.getStatus() != Trip.Status.RUNNING) {
			throw new BusinessException("Only a running trip can be completed");
		}
		trip.setStatus(Trip.Status.COMPLETED);
		trip.setEndTime(LocalDateTime.now());
		trip.getCableCar().setStatus(CableCar.Status.READY);
		return tripRepository.save(trip);
	}

	private Trip findById(Long id) {
		return tripRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + id));
	}
}