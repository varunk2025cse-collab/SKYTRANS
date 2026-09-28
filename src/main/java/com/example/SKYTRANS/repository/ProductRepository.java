package com.example.SKYTRANS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByName(String name);
}