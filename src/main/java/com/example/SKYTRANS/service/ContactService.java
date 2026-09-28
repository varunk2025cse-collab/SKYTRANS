package com.example.SKYTRANS.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.repository.ContactRepository;

@Service
public class ContactService {
	private final ContactRepository contactRepository;

	public ContactService(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

	public List<Contact> findAll() { return contactRepository.findAll(); }

	public Contact findById(Long id) {
		return contactRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
	}

	public Contact save(Contact contact) { return contactRepository.save(contact); }

	public Contact update(Long id, Contact contact) {
		findById(id);
		contact.setId(id);
		return contactRepository.save(contact);
	}

	public void delete(Long id) {
		contactRepository.delete(findById(id));
	}
}