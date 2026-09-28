package com.example.SKYTRANS.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.PurchaseOrder;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.repository.PurchaseOrderRepository;
import com.example.SKYTRANS.repository.VendorBillRepository;

@Service
public class VendorBillService {
	private final VendorBillRepository vendorBillRepository;
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final AccountingService accountingService;

	public VendorBillService(VendorBillRepository vendorBillRepository, PurchaseOrderRepository purchaseOrderRepository,
			AccountingService accountingService) {
		this.vendorBillRepository = vendorBillRepository;
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.accountingService = accountingService;
	}

	public List<VendorBill> findAll() { return vendorBillRepository.findAll(); }

	@Transactional
	public VendorBill create(VendorBill request) {
		Long orderId = request.getPurchaseOrder() == null ? null : request.getPurchaseOrder().getId();
		PurchaseOrder purchaseOrder = orderId == null ? null : purchaseOrderRepository.findById(orderId).orElse(null);
		if (purchaseOrder == null) {
			throw new ResourceNotFoundException("Purchase order not found");
		}
		if (vendorBillRepository.existsByPurchaseOrderId(orderId)) {
			throw new BusinessException("A vendor bill already exists for this purchase order");
		}
		if (purchaseOrder.getStatus() != PurchaseOrder.Status.APPROVED) {
			throw new BusinessException("Approve the purchase order before creating its bill");
		}
		VendorBill bill = new VendorBill();
		bill.setPurchaseOrder(purchaseOrder);
		bill.setVendor(purchaseOrder.getVendor());
		bill.setAmount(purchaseOrder.getTotalAmount());
		bill.setBillDate(request.getBillDate() == null ? LocalDate.now() : request.getBillDate());
		bill.setStatus(VendorBill.Status.UNPAID);
		purchaseOrder.setStatus(PurchaseOrder.Status.BILLED);
		purchaseOrderRepository.save(purchaseOrder);
		VendorBill savedBill = vendorBillRepository.save(bill);
		accountingService.postVendorBill(savedBill);
		return savedBill;
	}
}