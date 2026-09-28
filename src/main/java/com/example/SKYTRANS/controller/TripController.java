package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Trip;
import com.example.SKYTRANS.service.TripService;

@RestController
@RequestMapping("/api/trips")
public class TripController {
	private final TripService tripService;

	public TripController(TripService tripService) { this.tripService = tripService; }

	@GetMapping
	public List<Trip> findAll() { return tripService.findAll(); }

	@PostMapping
	public Trip create(@Valid @RequestBody Trip trip) { return tripService.create(trip); }

	@PostMapping("/{id}/start")
	public Trip start(@PathVariable Long id) { return tripService.start(id); }

	@PostMapping("/{id}/complete")
	public Trip complete(@PathVariable Long id) { return tripService.complete(id); }
}