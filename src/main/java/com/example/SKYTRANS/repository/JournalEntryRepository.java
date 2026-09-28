package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.JournalEntry;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> { }