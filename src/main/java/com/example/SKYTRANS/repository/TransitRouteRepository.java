package com.example.SKYTRANS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.TransitRoute;

public interface TransitRouteRepository extends JpaRepository<TransitRoute, Long> {
	Optional<TransitRoute> findByRouteCode(String routeCode);
}