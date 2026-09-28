package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.AnalyticAccount;
import com.example.SKYTRANS.service.BudgetService;

@RestController
@RequestMapping("/api/analytic-accounts")
public class AnalyticAccountController {
	private final BudgetService budgetService;

	public AnalyticAccountController(BudgetService budgetService) { this.budgetService = budgetService; }

	@GetMapping
	public List<AnalyticAccount> findAll() { return budgetService.findAllAnalyticAccounts(); }

	@PostMapping
	public AnalyticAccount create(@Valid @RequestBody AnalyticAccount account) {
		return budgetService.createAnalyticAccount(account);
	}
}