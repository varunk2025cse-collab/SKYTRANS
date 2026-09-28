package com.example.SKYTRANS.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "budgets")
public class Budget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "analytic_account_id")
	private AnalyticAccount analyticAccount;

	@Min(2000)
	private int budgetYear;

	@NotNull
	@PositiveOrZero
	private BigDecimal plannedRevenue;

	@NotNull
	@PositiveOrZero
	private BigDecimal electricityBudget;

	@NotNull
	@PositiveOrZero
	private BigDecimal maintenanceBudget;

	public Budget() { }

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public AnalyticAccount getAnalyticAccount() { return analyticAccount; }
	public void setAnalyticAccount(AnalyticAccount analyticAccount) { this.analyticAccount = analyticAccount; }
	public int getBudgetYear() { return budgetYear; }
	public void setBudgetYear(int budgetYear) { this.budgetYear = budgetYear; }
	public BigDecimal getPlannedRevenue() { return plannedRevenue; }
	public void setPlannedRevenue(BigDecimal plannedRevenue) { this.plannedRevenue = plannedRevenue; }
	public BigDecimal getElectricityBudget() { return electricityBudget; }
	public void setElectricityBudget(BigDecimal electricityBudget) { this.electricityBudget = electricityBudget; }
	public BigDecimal getMaintenanceBudget() { return maintenanceBudget; }
	public void setMaintenanceBudget(BigDecimal maintenanceBudget) { this.maintenanceBudget = maintenanceBudget; }
}