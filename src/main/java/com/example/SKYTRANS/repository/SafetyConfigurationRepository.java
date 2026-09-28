package com.example.SKYTRANS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.SafetyConfiguration;

public interface SafetyConfigurationRepository extends JpaRepository<SafetyConfiguration, Long> {
	Optional<SafetyConfiguration> findFirstByOrderByIdAsc();
}
