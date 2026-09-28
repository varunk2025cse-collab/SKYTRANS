package com.example.SKYTRANS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SKYTRANS.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
	Optional<Contact> findByName(String name);
}