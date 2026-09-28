package com.example.SKYTRANS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.model.PurchaseOrder;
import com.example.SKYTRANS.repository.ContactRepository;
import com.example.SKYTRANS.repository.ProductRepository;
import com.example.SKYTRANS.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderService {
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final ContactRepository contactRepository;
	private final ProductRepository productRepository;

	public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, ContactRepository contactRepository,
			ProductRepository productRepository) {
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.contactRepository = contactRepository;
		this.productRepository = productRepository;
	}

	public List<PurchaseOrder> findAll() { return purchaseOrderRepository.findAll(); }

	public PurchaseOrder create(PurchaseOrder purchaseOrder) {
		Long vendorId = purchaseOrder.getVendor() == null ? null : purchaseOrder.getVendor().getId();
		Long productId = purchaseOrder.getProduct() == null ? null : purchaseOrder.getProduct().getId();
		Contact vendor = vendorId == null ? null : contactRepository.findById(vendorId).orElse(null);
		Product product = productId == null ? null : productRepository.findById(productId).orElse(null);
		if (vendor == null || vendor.getType() != Contact.ContactType.VENDOR) {
			throw new BusinessException("A valid vendor contact is required");
		}
		if (product == null) {
			throw new ResourceNotFoundException("Product not found");
		}
		if (purchaseOrder.getQuantity() < 1) {
			throw new BusinessException("Purchase quantity must be at least 1");
		}
		BigDecimal unitPrice = purchaseOrder.getUnitPrice() == null ? product.getUnitPrice() : purchaseOrder.getUnitPrice();
		if (unitPrice == null || unitPrice.signum() <= 0) {
			throw new BusinessException("Unit price must be greater than zero");
		}
		purchaseOrder.setVendor(vendor);
		purchaseOrder.setProduct(product);
		purchaseOrder.setUnitPrice(unitPrice);
		purchaseOrder.setTotalAmount(unitPrice.multiply(BigDecimal.valueOf(purchaseOrder.getQuantity())));
		purchaseOrder.setOrderDate(purchaseOrder.getOrderDate() == null ? LocalDate.now() : purchaseOrder.getOrderDate());
		purchaseOrder.setStatus(PurchaseOrder.Status.CREATED);
		return purchaseOrderRepository.save(purchaseOrder);
	}

	public PurchaseOrder approve(Long id) {
		PurchaseOrder purchaseOrder = findById(id);
		if (purchaseOrder.getStatus() != PurchaseOrder.Status.CREATED) {
			throw new BusinessException("Only a created purchase order can be approved");
		}
		purchaseOrder.setStatus(PurchaseOrder.Status.APPROVED);
		return purchaseOrderRepository.save(purchaseOrder);
	}

	public PurchaseOrder cancel(Long id) {
		PurchaseOrder purchaseOrder = findById(id);
		if (purchaseOrder.getStatus() == PurchaseOrder.Status.BILLED
				|| purchaseOrder.getStatus() == PurchaseOrder.Status.CANCELLED) {
			throw new BusinessException("This purchase order cannot be cancelled");
		}
		purchaseOrder.setStatus(PurchaseOrder.Status.CANCELLED);
		return purchaseOrderRepository.save(purchaseOrder);
	}

	private PurchaseOrder findById(Long id) {
		return purchaseOrderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + id));
	}
}