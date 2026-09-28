package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.AnalyticAccount;
import com.example.SKYTRANS.model.Budget;
import com.example.SKYTRANS.repository.AnalyticAccountRepository;
import com.example.SKYTRANS.repository.BudgetRepository;

@Service
public class BudgetService {
	private final BudgetRepository budgetRepository;
	private final AnalyticAccountRepository analyticAccountRepository;

	public BudgetService(BudgetRepository budgetRepository, AnalyticAccountRepository analyticAccountRepository) {
		this.budgetRepository = budgetRepository;
		this.analyticAccountRepository = analyticAccountRepository;
	}

	public List<Budget> findAll() { return budgetRepository.findAll(); }
	public List<AnalyticAccount> findAllAnalyticAccounts() { return analyticAccountRepository.findAll(); }
	public AnalyticAccount createAnalyticAccount(AnalyticAccount account) { return analyticAccountRepository.save(account); }

	public Budget create(Budget budget) {
		Long analyticId = budget.getAnalyticAccount() == null ? null : budget.getAnalyticAccount().getId();
		AnalyticAccount analyticAccount = analyticId == null ? null
				: analyticAccountRepository.findById(analyticId).orElse(null);
		if (analyticAccount == null) {
			throw new ResourceNotFoundException("Analytic account not found");
		}
		if (budgetRepository.existsByAnalyticAccountIdAndBudgetYear(analyticId, budget.getBudgetYear())) {
			throw new BusinessException("A budget already exists for this corridor and year");
		}
		budget.setAnalyticAccount(analyticAccount);
		return budgetRepository.save(budget);
	}
}