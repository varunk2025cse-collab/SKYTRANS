package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.repository.AccountRepository;

@Service
public class AccountService {
	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public List<Account> findAll() { return accountRepository.findAll(); }
	public Account save(Account account) { return accountRepository.save(account); }
}