package com.example.SKYTRANS.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.model.SalesOrder;
import com.example.SKYTRANS.repository.InvoiceRepository;
import com.example.SKYTRANS.repository.SalesOrderRepository;

@Service
public class InvoiceService {
	private final InvoiceRepository invoiceRepository;
	private final SalesOrderRepository salesOrderRepository;
	private final AccountingService accountingService;

	public InvoiceService(InvoiceRepository invoiceRepository, SalesOrderRepository salesOrderRepository,
			AccountingService accountingService) {
		this.invoiceRepository = invoiceRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.accountingService = accountingService;
	}

	public List<Invoice> findAll() { return invoiceRepository.findAll(); }

	@Transactional
	public Invoice create(Invoice request) {
		Long orderId = request.getSalesOrder() == null ? null : request.getSalesOrder().getId();
		SalesOrder salesOrder = orderId == null ? null : salesOrderRepository.findById(orderId).orElse(null);
		if (salesOrder == null) {
			throw new ResourceNotFoundException("Sales order not found");
		}
		if (invoiceRepository.existsBySalesOrderId(orderId)) {
			throw new BusinessException("An invoice already exists for this sales order");
		}
		if (salesOrder.getStatus() != SalesOrder.Status.CREATED) {
			throw new BusinessException("Only a new sales order can be invoiced");
		}
		Invoice invoice = new Invoice();
		invoice.setSalesOrder(salesOrder);
		invoice.setCustomer(salesOrder.getCustomer());
		invoice.setPassengerTrips(salesOrder.getPassengerTrips());
		invoice.setAmount(salesOrder.getTotalAmount());
		invoice.setInvoiceDate(request.getInvoiceDate() == null ? LocalDate.now() : request.getInvoiceDate());
		invoice.setStatus(Invoice.Status.UNPAID);
		salesOrder.setStatus(SalesOrder.Status.INVOICED);
		salesOrderRepository.save(salesOrder);
		Invoice savedInvoice = invoiceRepository.save(invoice);
		accountingService.postCustomerInvoice(savedInvoice);
		return savedInvoice;
	}
}