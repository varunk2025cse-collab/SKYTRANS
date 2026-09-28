package com.example.SKYTRANS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.JournalEntry;
import com.example.SKYTRANS.service.AccountingService;

@RestController
@RequestMapping("/api/journal-entries")
public class JournalEntryController {
	private final AccountingService accountingService;

	public JournalEntryController(AccountingService accountingService) { this.accountingService = accountingService; }

	@GetMapping
	public List<JournalEntry> findAll() { return accountingService.findAllEntries(); }
}