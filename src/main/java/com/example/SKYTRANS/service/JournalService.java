package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.model.Journal;
import com.example.SKYTRANS.repository.JournalRepository;

@Service
public class JournalService {
	private final JournalRepository journalRepository;

	public JournalService(JournalRepository journalRepository) {
		this.journalRepository = journalRepository;
	}

	public List<Journal> findAll() { return journalRepository.findAll(); }
	public Journal save(Journal journal) { return journalRepository.save(journal); }
}