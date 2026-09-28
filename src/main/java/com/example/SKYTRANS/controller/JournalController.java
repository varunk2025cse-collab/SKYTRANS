package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Journal;
import com.example.SKYTRANS.service.JournalService;

@RestController
@RequestMapping("/api/journals")
public class JournalController {
	private final JournalService journalService;

	public JournalController(JournalService journalService) {
		this.journalService = journalService;
	}

	@GetMapping
	public List<Journal> findAll() { return journalService.findAll(); }

	@PostMapping
	public Journal create(@Valid @RequestBody Journal journal) { return journalService.save(journal); }
}