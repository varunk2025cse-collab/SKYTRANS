package com.example.SKYTRANS.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.service.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
	private final ContactService contactService;

	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@GetMapping
	public List<Contact> findAll() { return contactService.findAll(); }

	@GetMapping("/{id}")
	public Contact findById(@PathVariable Long id) { return contactService.findById(id); }

	@PostMapping
	public Contact create(@Valid @RequestBody Contact contact) { return contactService.save(contact); }

	@PutMapping("/{id}")
	public Contact update(@PathVariable Long id, @Valid @RequestBody Contact contact) {
		return contactService.update(id, contact);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		contactService.delete(id);
		return ResponseEntity.noContent().build();
	}
}