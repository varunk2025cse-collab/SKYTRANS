package com.example.SKYTRANS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.CableCar;

public interface CableCarRepository extends JpaRepository<CableCar, Long> {
	Optional<CableCar> findByVehicleNumber(String vehicleNumber);
}