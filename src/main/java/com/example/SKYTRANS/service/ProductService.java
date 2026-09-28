package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.repository.AccountRepository;
import com.example.SKYTRANS.repository.ProductRepository;

@Service
public class ProductService {
	private final ProductRepository productRepository;
	private final AccountRepository accountRepository;

	public ProductService(ProductRepository productRepository, AccountRepository accountRepository) {
		this.productRepository = productRepository;
		this.accountRepository = accountRepository;
	}

	public List<Product> findAll() { return productRepository.findAll(); }

	public Product findById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
	}

	public Product save(Product product) {
		product.setAccount(findLedgerAccount(product));
		return productRepository.save(product);
	}

	public Product update(Long id, Product product) {
		findById(id);
		product.setId(id);
		return save(product);
	}

	public void delete(Long id) {
		productRepository.delete(findById(id));
	}

	private Account findLedgerAccount(Product product) {
		Long accountId = product.getAccount() == null ? null : product.getAccount().getId();
		if (accountId == null) {
			throw new BusinessException("Choose the ledger account for this product");
		}
		return accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Ledger account not found: " + accountId));
	}
}
