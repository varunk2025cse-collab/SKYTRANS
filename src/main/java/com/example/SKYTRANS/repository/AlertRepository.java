package com.example.SKYTRANS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
	List<Alert> findByResolvedFalseOrderByCreatedAtDesc();
}