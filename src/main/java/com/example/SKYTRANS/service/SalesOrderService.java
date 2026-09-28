package com.example.SKYTRANS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.model.SalesOrder;
import com.example.SKYTRANS.repository.ContactRepository;
import com.example.SKYTRANS.repository.ProductRepository;
import com.example.SKYTRANS.repository.SalesOrderRepository;

@Service
public class SalesOrderService {
	private final SalesOrderRepository salesOrderRepository;
	private final ContactRepository contactRepository;
	private final ProductRepository productRepository;
	private final PassengerReportService passengerReportService;

	public SalesOrderService(SalesOrderRepository salesOrderRepository, ContactRepository contactRepository,
			ProductRepository productRepository, PassengerReportService passengerReportService) {
		this.salesOrderRepository = salesOrderRepository;
		this.contactRepository = contactRepository;
		this.productRepository = productRepository;
		this.passengerReportService = passengerReportService;
	}

	public List<SalesOrder> findAll() { return salesOrderRepository.findAll(); }

	public SalesOrder create(SalesOrder salesOrder) {
		Long customerId = salesOrder.getCustomer() == null ? null : salesOrder.getCustomer().getId();
		Long productId = salesOrder.getProduct() == null ? null : salesOrder.getProduct().getId();
		Contact customer = customerId == null ? null : contactRepository.findById(customerId).orElse(null);
		Product product = productId == null ? null : productRepository.findById(productId).orElse(null);
		if (customer == null || customer.getType() != Contact.ContactType.CUSTOMER) {
			throw new BusinessException("A valid customer contact is required");
		}
		if (product == null) {
			throw new ResourceNotFoundException("Product not found");
		}
		LocalDate orderDate = salesOrder.getOrderDate() == null ? LocalDate.now() : salesOrder.getOrderDate();
		int passengerTrips = salesOrder.getPassengerTrips();
		if (passengerTrips == 0) {
			passengerTrips = Math.toIntExact(passengerReportService.getMonthlyPassengerCount(YearMonth.from(orderDate)));
		}
		if (passengerTrips < 1) {
			throw new BusinessException("At least one passenger trip is required for billing");
		}
		BigDecimal rate = salesOrder.getRatePerPassenger() == null ? product.getUnitPrice() : salesOrder.getRatePerPassenger();
		if (rate == null || rate.signum() <= 0) {
			throw new BusinessException("Rate per passenger must be greater than zero");
		}
		salesOrder.setCustomer(customer);
		salesOrder.setProduct(product);
		salesOrder.setPassengerTrips(passengerTrips);
		salesOrder.setRatePerPassenger(rate);
		salesOrder.setTotalAmount(rate.multiply(BigDecimal.valueOf(passengerTrips)));
		salesOrder.setOrderDate(orderDate);
		salesOrder.setStatus(SalesOrder.Status.CREATED);
		return salesOrderRepository.save(salesOrder);
	}
}