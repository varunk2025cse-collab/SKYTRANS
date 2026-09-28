package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.TransitRoute;
import com.example.SKYTRANS.service.TransitRouteService;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
	private final TransitRouteService routeService;

	public RouteController(TransitRouteService routeService) { this.routeService = routeService; }

	@GetMapping
	public List<TransitRoute> findAll() { return routeService.findAll(); }

	@PostMapping
	public TransitRoute create(@Valid @RequestBody TransitRoute route) { return routeService.save(route); }
}