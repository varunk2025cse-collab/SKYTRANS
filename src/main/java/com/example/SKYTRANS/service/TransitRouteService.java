package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Terminal;
import com.example.SKYTRANS.model.TransitRoute;
import com.example.SKYTRANS.repository.TerminalRepository;
import com.example.SKYTRANS.repository.TransitRouteRepository;

@Service
public class TransitRouteService {
	private final TransitRouteRepository routeRepository;
	private final TerminalRepository terminalRepository;

	public TransitRouteService(TransitRouteRepository routeRepository, TerminalRepository terminalRepository) {
		this.routeRepository = routeRepository;
		this.terminalRepository = terminalRepository;
	}

	public List<TransitRoute> findAll() { return routeRepository.findAll(); }

	public TransitRoute save(TransitRoute route) {
		Terminal source = terminalRepository.findById(route.getSourceTerminal().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Source terminal not found"));
		Terminal destination = terminalRepository.findById(route.getDestinationTerminal().getId())
				.orElseThrow(() -> new ResourceNotFoundException("Destination terminal not found"));
		if (source.getId().equals(destination.getId())) {
			throw new BusinessException("Source and destination terminals must be different");
		}
		route.setSourceTerminal(source);
		route.setDestinationTerminal(destination);
		return routeRepository.save(route);
	}
}