package com.example.SKYTRANS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
	List<Trip> findByCableCarId(Long cableCarId);
}