package com.example.SKYTRANS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.Journal;
import com.example.SKYTRANS.model.JournalEntry;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.repository.AccountRepository;
import com.example.SKYTRANS.repository.JournalEntryRepository;
import com.example.SKYTRANS.repository.JournalRepository;

// Creates double-entry journal entries for invoices, bills and payments.
// Bank, receivable and payable account codes come from application.properties;
// income and expense accounts come from the product's ledger account.
@Service
public class AccountingService {
	private final JournalEntryRepository entryRepository;
	private final AccountRepository accountRepository;
	private final JournalRepository journalRepository;
	private final String bankAccountCode;
	private final String receivableAccountCode;
	private final String payableAccountCode;

	public AccountingService(JournalEntryRepository entryRepository, AccountRepository accountRepository,
			JournalRepository journalRepository,
			@Value("${skytrans.accounts.bank}") String bankAccountCode,
			@Value("${skytrans.accounts.receivable}") String receivableAccountCode,
			@Value("${skytrans.accounts.payable}") String payableAccountCode) {
		this.entryRepository = entryRepository;
		this.accountRepository = accountRepository;
		this.journalRepository = journalRepository;
		this.bankAccountCode = bankAccountCode;
		this.receivableAccountCode = receivableAccountCode;
		this.payableAccountCode = payableAccountCode;
	}

	public List<JournalEntry> findAllEntries() { return entryRepository.findAll(); }

	// Debit Customer Receivables, Credit the product's income account
	public JournalEntry postCustomerInvoice(Invoice invoice) {
		Account receivable = findAccount(receivableAccountCode);
		Account revenue = getProductAccount(invoice.getSalesOrder().getProduct());
		if (revenue.getAccountType() != Account.AccountType.INCOME) {
			throw new BusinessException("Product '" + invoice.getSalesOrder().getProduct().getName()
					+ "' must use an INCOME ledger account to be invoiced");
		}
		return createEntry(LocalDate.now(), "Customer invoice " + invoice.getId(), Journal.JournalType.SALES,
				receivable, revenue, invoice.getAmount());
	}

	// Debit the product's expense (or asset) account, Credit Vendor Payables
	public JournalEntry postVendorBill(VendorBill bill) {
		Account expense = getProductAccount(bill.getPurchaseOrder().getProduct());
		if (expense.getAccountType() != Account.AccountType.EXPENSE
				&& expense.getAccountType() != Account.AccountType.ASSET) {
			throw new BusinessException("Product '" + bill.getPurchaseOrder().getProduct().getName()
					+ "' must use an EXPENSE or ASSET ledger account to be billed");
		}
		Account payable = findAccount(payableAccountCode);
		return createEntry(LocalDate.now(), "Vendor bill " + bill.getId(), Journal.JournalType.PURCHASE,
				expense, payable, bill.getAmount());
	}

	// Debit Bank, Credit Customer Receivables
	public JournalEntry postCustomerPayment(BigDecimal amount, Long paymentId) {
		Account bank = findAccount(bankAccountCode);
		Account receivable = findAccount(receivableAccountCode);
		return createEntry(LocalDate.now(), "Customer payment " + paymentId, Journal.JournalType.BANK,
				bank, receivable, amount);
	}

	// Debit Vendor Payables, Credit Bank
	public JournalEntry postVendorPayment(BigDecimal amount, Long paymentId) {
		Account payable = findAccount(payableAccountCode);
		Account bank = findAccount(bankAccountCode);
		return createEntry(LocalDate.now(), "Vendor payment " + paymentId, Journal.JournalType.BANK,
				payable, bank, amount);
	}

	public JournalEntry createEntry(LocalDate date, String description, Journal.JournalType journalType,
			Account debitAccount, Account creditAccount, BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new BusinessException("Journal amount must be greater than zero");
		}
		if (debitAccount == null || creditAccount == null || debitAccount.getId().equals(creditAccount.getId())) {
			throw new BusinessException("Debit and credit accounts must be different");
		}
		Journal journal = journalRepository.findByType(journalType)
				.orElseGet(() -> journalRepository.save(new Journal(journalName(journalType), journalType)));
		JournalEntry entry = new JournalEntry();
		entry.setDate(date == null ? LocalDate.now() : date);
		entry.setDescription(description);
		entry.setJournal(journal);
		entry.setDebitAccount(debitAccount);
		entry.setCreditAccount(creditAccount);
		entry.setAmount(amount);
		return entryRepository.save(entry);
	}

	// Looks up a configured account in the chart of accounts
	private Account findAccount(String accountCode) {
		return accountRepository.findByAccountCode(accountCode)
				.orElseThrow(() -> new BusinessException("Account " + accountCode + " is missing from the chart of accounts"));
	}

	private Account getProductAccount(Product product) {
		if (product.getAccount() == null) {
			throw new BusinessException("Product '" + product.getName() + "' has no ledger account");
		}
		return product.getAccount();
	}

	private String journalName(Journal.JournalType type) {
		if (type == Journal.JournalType.SALES) {
			return "Sales Journal";
		}
		if (type == Journal.JournalType.PURCHASE) {
			return "Purchase Journal";
		}
		if (type == Journal.JournalType.CASH) {
			return "Cash Journal";
		}
		return "Bank Journal";
	}
}
