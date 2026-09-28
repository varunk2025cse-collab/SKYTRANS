package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Budget;
import com.example.SKYTRANS.service.BudgetService;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
	private final BudgetService budgetService;

	public BudgetController(BudgetService budgetService) { this.budgetService = budgetService; }

	@GetMapping
	public List<Budget> findAll() { return budgetService.findAll(); }

	@PostMapping
	public Budget create(@Valid @RequestBody Budget budget) { return budgetService.create(budget); }
}