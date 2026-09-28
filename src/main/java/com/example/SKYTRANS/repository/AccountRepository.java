package com.example.SKYTRANS.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	java.util.Optional<Account> findByAccountCode(String accountCode);
	java.util.Optional<Account> findByAccountName(String accountName);
}