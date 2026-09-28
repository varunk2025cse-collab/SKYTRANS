package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Terminal;
import com.example.SKYTRANS.service.TerminalService;

@RestController
@RequestMapping("/api/cablecar/terminals")
public class TerminalController {
	private final TerminalService terminalService;

	public TerminalController(TerminalService terminalService) { this.terminalService = terminalService; }

	@GetMapping
	public List<Terminal> findAll() { return terminalService.findAll(); }

	@PostMapping
	public Terminal create(@Valid @RequestBody Terminal terminal) { return terminalService.save(terminal); }
}