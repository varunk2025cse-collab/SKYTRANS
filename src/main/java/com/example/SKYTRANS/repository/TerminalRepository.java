package com.example.SKYTRANS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Terminal;

public interface TerminalRepository extends JpaRepository<Terminal, Long> {
	Optional<Terminal> findByTerminalCode(String terminalCode);
}