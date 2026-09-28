package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.repository.CableCarRepository;

@Service
public class CableCarService {
	private final CableCarRepository cableCarRepository;

	public CableCarService(CableCarRepository cableCarRepository) {
		this.cableCarRepository = cableCarRepository;
	}

	public List<CableCar> findAll() { return cableCarRepository.findAll(); }

	public CableCar findById(Long id) {
		return cableCarRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cable car not found: " + id));
	}

	public CableCar save(CableCar cableCar) { return cableCarRepository.save(cableCar); }

	public CableCar update(Long id, CableCar cableCar) {
		findById(id);
		cableCar.setId(id);
		return cableCarRepository.save(cableCar);
	}

	public void delete(Long id) { cableCarRepository.delete(findById(id)); }
}