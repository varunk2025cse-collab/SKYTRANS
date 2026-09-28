package com.example.SKYTRANS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Telemetry;

public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
	List<Telemetry> findByCableCarIdOrderByTimestampDesc(Long cableCarId);
	List<Telemetry> findTop5ByOrderByTimestampDesc();
}