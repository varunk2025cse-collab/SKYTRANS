package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.service.CableCarService;

@RestController
@RequestMapping("/api/cablecars")
public class CableCarController {
	private final CableCarService cableCarService;

	public CableCarController(CableCarService cableCarService) { this.cableCarService = cableCarService; }

	@GetMapping
	public List<CableCar> findAll() { return cableCarService.findAll(); }

	@GetMapping("/{id}")
	public CableCar findById(@PathVariable Long id) { return cableCarService.findById(id); }

	@PostMapping
	public CableCar create(@Valid @RequestBody CableCar cableCar) { return cableCarService.save(cableCar); }

	@PutMapping("/{id}")
	public CableCar update(@PathVariable Long id, @Valid @RequestBody CableCar cableCar) {
		return cableCarService.update(id, cableCar);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		cableCarService.delete(id);
		return ResponseEntity.noContent().build();
	}
}