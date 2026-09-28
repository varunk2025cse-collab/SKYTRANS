package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	boolean existsByAnalyticAccountIdAndBudgetYear(Long analyticAccountId, int budgetYear);
}
