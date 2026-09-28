package com.example.SKYTRANS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.Budget;
import com.example.SKYTRANS.model.JournalEntry;
import com.example.SKYTRANS.repository.AccountRepository;
import com.example.SKYTRANS.repository.BudgetRepository;
import com.example.SKYTRANS.repository.JournalEntryRepository;

@Service
public class ReportService {
	private final AccountRepository accountRepository;
	private final JournalEntryRepository entryRepository;
	private final BudgetRepository budgetRepository;
	private final String electricityAccountCode;
	private final String maintenanceAccountCode;

	public ReportService(AccountRepository accountRepository, JournalEntryRepository entryRepository,
			BudgetRepository budgetRepository,
			@Value("${skytrans.accounts.electricity-expense}") String electricityAccountCode,
			@Value("${skytrans.accounts.maintenance-expense}") String maintenanceAccountCode) {
		this.accountRepository = accountRepository;
		this.entryRepository = entryRepository;
		this.budgetRepository = budgetRepository;
		this.electricityAccountCode = electricityAccountCode;
		this.maintenanceAccountCode = maintenanceAccountCode;
	}

	public Map<String, Object> getBalanceSheet() {
		Map<String, BigDecimal> assets = new LinkedHashMap<>();
		Map<String, BigDecimal> liabilities = new LinkedHashMap<>();
		BigDecimal totalAssets = BigDecimal.ZERO;
		BigDecimal totalLiabilities = BigDecimal.ZERO;
		List<JournalEntry> entries = entryRepository.findAll();
		for (Account account : accountRepository.findAll()) {
			if (account.getAccountType() == Account.AccountType.ASSET) {
				BigDecimal balance = getAccountBalance(account, entries);
				assets.put(account.getAccountName(), balance);
				totalAssets = totalAssets.add(balance);
			} else if (account.getAccountType() == Account.AccountType.LIABILITY) {
				BigDecimal balance = getAccountBalance(account, entries);
				liabilities.put(account.getAccountName(), balance);
				totalLiabilities = totalLiabilities.add(balance);
			}
		}
		// Profit not yet paid out belongs to the owners (equity), so Assets = Liabilities + Equity
		BigDecimal currentEarnings = (BigDecimal) getProfitAndLoss().get("netProfit");
		Map<String, Object> report = new LinkedHashMap<>();
		report.put("assets", assets);
		report.put("totalAssets", totalAssets);
		report.put("liabilities", liabilities);
		report.put("totalLiabilities", totalLiabilities);
		report.put("currentEarnings", currentEarnings);
		report.put("totalLiabilitiesAndEquity", totalLiabilities.add(currentEarnings));
		return report;
	}

	public Map<String, Object> getProfitAndLoss() {
		Map<String, BigDecimal> income = new LinkedHashMap<>();
		Map<String, BigDecimal> expenses = new LinkedHashMap<>();
		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpenses = BigDecimal.ZERO;
		List<JournalEntry> entries = entryRepository.findAll();
		for (Account account : accountRepository.findAll()) {
			if (account.getAccountType() == Account.AccountType.INCOME) {
				BigDecimal balance = getAccountBalance(account, entries);
				income.put(account.getAccountName(), balance);
				totalIncome = totalIncome.add(balance);
			} else if (account.getAccountType() == Account.AccountType.EXPENSE) {
				BigDecimal balance = getAccountBalance(account, entries);
				expenses.put(account.getAccountName(), balance);
				totalExpenses = totalExpenses.add(balance);
			}
		}
		Map<String, Object> report = new LinkedHashMap<>();
		report.put("income", income);
		report.put("totalIncome", totalIncome);
		report.put("expenses", expenses);
		report.put("totalExpenses", totalExpenses);
		report.put("netProfit", totalIncome.subtract(totalExpenses));
		return report;
	}

	public List<Map<String, Object>> getBudgetReport() {
		List<Map<String, Object>> report = new ArrayList<>();
		List<JournalEntry> entries = entryRepository.findAll();
		for (Budget budget : budgetRepository.findAll()) {
			int year = budget.getBudgetYear();
			BigDecimal actualRevenue = getYearlyIncome(year, entries);
			BigDecimal actualElectricity = getYearlyExpense(electricityAccountCode, year, entries);
			BigDecimal actualMaintenance = getYearlyExpense(maintenanceAccountCode, year, entries);

			Map<String, Object> row = new LinkedHashMap<>();
			row.put("transitCorridor", budget.getAnalyticAccount().getName());
			row.put("budgetYear", budget.getBudgetYear());
			row.put("plannedRevenue", budget.getPlannedRevenue());
			row.put("actualRevenue", actualRevenue);
			row.put("revenueVariance", actualRevenue.subtract(budget.getPlannedRevenue()));
			row.put("electricityBudget", budget.getElectricityBudget());
			row.put("actualElectricityExpense", actualElectricity);
			row.put("electricityVariance", actualElectricity.subtract(budget.getElectricityBudget()));
			row.put("maintenanceBudget", budget.getMaintenanceBudget());
			row.put("actualMaintenanceExpense", actualMaintenance);
			row.put("maintenanceVariance", actualMaintenance.subtract(budget.getMaintenanceBudget()));
			report.add(row);
		}
		return report;
	}

	public Map<String, BigDecimal> getCurrentMonthTotals() {
		BigDecimal income = BigDecimal.ZERO;
		BigDecimal expenses = BigDecimal.ZERO;
		YearMonth currentMonth = YearMonth.now();
		for (JournalEntry entry : entryRepository.findAll()) {
			if (!YearMonth.from(entry.getDate()).equals(currentMonth)) {
				continue;
			}
			if (entry.getCreditAccount().getAccountType() == Account.AccountType.INCOME) {
				income = income.add(entry.getAmount());
			}
			if (entry.getDebitAccount().getAccountType() == Account.AccountType.EXPENSE) {
				expenses = expenses.add(entry.getAmount());
			}
		}
		Map<String, BigDecimal> totals = new LinkedHashMap<>();
		totals.put("revenue", income);
		totals.put("expenses", expenses);
		return totals;
	}

	// Income for the year = credits to income accounts minus debits to income accounts
	private BigDecimal getYearlyIncome(int year, List<JournalEntry> entries) {
		BigDecimal total = BigDecimal.ZERO;
		for (JournalEntry entry : entries) {
			if (entry.getDate().getYear() != year) {
				continue;
			}
			if (entry.getCreditAccount().getAccountType() == Account.AccountType.INCOME) {
				total = total.add(entry.getAmount());
			}
			if (entry.getDebitAccount().getAccountType() == Account.AccountType.INCOME) {
				total = total.subtract(entry.getAmount());
			}
		}
		return total;
	}

	// Expense for the year on one account = debits minus credits to that account code
	private BigDecimal getYearlyExpense(String accountCode, int year, List<JournalEntry> entries) {
		BigDecimal total = BigDecimal.ZERO;
		for (JournalEntry entry : entries) {
			if (entry.getDate().getYear() != year) {
				continue;
			}
			if (entry.getDebitAccount().getAccountCode().equals(accountCode)) {
				total = total.add(entry.getAmount());
			}
			if (entry.getCreditAccount().getAccountCode().equals(accountCode)) {
				total = total.subtract(entry.getAmount());
			}
		}
		return total;
	}

	private BigDecimal getAccountBalance(Account account, List<JournalEntry> entries) {
		BigDecimal balance = account.getOpeningBalance();
		for (JournalEntry entry : entries) {
			boolean debitNormal = account.getAccountType() == Account.AccountType.ASSET
					|| account.getAccountType() == Account.AccountType.EXPENSE;
			if (entry.getDebitAccount().getId().equals(account.getId())) {
				balance = debitNormal ? balance.add(entry.getAmount()) : balance.subtract(entry.getAmount());
			}
			if (entry.getCreditAccount().getId().equals(account.getId())) {
				balance = debitNormal ? balance.subtract(entry.getAmount()) : balance.add(entry.getAmount());
			}
		}
		return balance;
	}
}