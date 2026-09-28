package com.example.SKYTRANS.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.model.Trip;
import com.example.SKYTRANS.repository.TripRepository;

@Service
public class PassengerReportService {
	private final TripRepository tripRepository;

	public PassengerReportService(TripRepository tripRepository) { this.tripRepository = tripRepository; }

	public long getDailyPassengerCount(LocalDate date) {
		long totalPassengers = 0;
		List<Trip> trips = tripRepository.findAll();
		for (Trip trip : trips) {
			if (trip.getStatus() == Trip.Status.COMPLETED && trip.getStartTime() != null
					&& trip.getStartTime().toLocalDate().equals(date)) {
				totalPassengers += trip.getPassengerCount();
			}
		}
		return totalPassengers;
	}

	public long getMonthlyPassengerCount(YearMonth month) {
		long totalPassengers = 0;
		List<Trip> trips = tripRepository.findAll();
		for (Trip trip : trips) {
			if (trip.getStatus() == Trip.Status.COMPLETED && trip.getStartTime() != null
					&& YearMonth.from(trip.getStartTime()).equals(month)) {
				totalPassengers += trip.getPassengerCount();
			}
		}
		return totalPassengers;
	}
}