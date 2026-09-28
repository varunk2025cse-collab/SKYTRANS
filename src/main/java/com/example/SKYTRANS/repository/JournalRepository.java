package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Journal;

public interface JournalRepository extends JpaRepository<Journal, Long> {
	java.util.Optional<Journal> findByType(Journal.JournalType type);
}