package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.model.Terminal;
import com.example.SKYTRANS.repository.TerminalRepository;

@Service
public class TerminalService {
	private final TerminalRepository terminalRepository;

	public TerminalService(TerminalRepository terminalRepository) {
		this.terminalRepository = terminalRepository;
	}

	public List<Terminal> findAll() { return terminalRepository.findAll(); }
	public Terminal save(Terminal terminal) { return terminalRepository.save(terminal); }
}