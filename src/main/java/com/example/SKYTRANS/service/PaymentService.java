package com.example.SKYTRANS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.exception.ResourceNotFoundException;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.model.Payment;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.repository.InvoiceRepository;
import com.example.SKYTRANS.repository.PaymentRepository;
import com.example.SKYTRANS.repository.VendorBillRepository;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final InvoiceRepository invoiceRepository;
	private final VendorBillRepository vendorBillRepository;
	private final AccountingService accountingService;

	public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository,
			VendorBillRepository vendorBillRepository, AccountingService accountingService) {
		this.paymentRepository = paymentRepository;
		this.invoiceRepository = invoiceRepository;
		this.vendorBillRepository = vendorBillRepository;
		this.accountingService = accountingService;
	}

	public List<Payment> findAll() { return paymentRepository.findAll(); }

	@Transactional
	public Payment receiveCustomerPayment(Payment request) {
		Long invoiceId = request.getInvoice() == null ? null : request.getInvoice().getId();
		Invoice invoice = invoiceId == null ? null : invoiceRepository.findById(invoiceId).orElse(null);
		if (invoice == null) {
			throw new ResourceNotFoundException("Invoice not found");
		}
		BigDecimal paid = paymentRepository.sumByInvoiceId(invoiceId);
		checkOutstanding(request.getAmount(), invoice.getAmount(), paid);
		request.setType(Payment.Type.CUSTOMER);
		request.setInvoice(invoice);
		request.setPaymentDate(request.getPaymentDate() == null ? LocalDate.now() : request.getPaymentDate());
		Payment payment = paymentRepository.save(request);
		BigDecimal newTotal = (paid == null ? BigDecimal.ZERO : paid).add(payment.getAmount());
		if (newTotal.compareTo(invoice.getAmount()) == 0) {
			invoice.setStatus(Invoice.Status.PAID);
			invoiceRepository.save(invoice);
		}
		accountingService.postCustomerPayment(payment.getAmount(), payment.getId());
		return payment;
	}

	@Transactional
	public Payment payVendor(Payment request) {
		Long billId = request.getVendorBill() == null ? null : request.getVendorBill().getId();
		VendorBill bill = billId == null ? null : vendorBillRepository.findById(billId).orElse(null);
		if (bill == null) {
			throw new ResourceNotFoundException("Vendor bill not found");
		}
		BigDecimal paid = paymentRepository.sumByVendorBillId(billId);
		checkOutstanding(request.getAmount(), bill.getAmount(), paid);
		request.setType(Payment.Type.VENDOR);
		request.setVendorBill(bill);
		request.setPaymentDate(request.getPaymentDate() == null ? LocalDate.now() : request.getPaymentDate());
		Payment payment = paymentRepository.save(request);
		BigDecimal newTotal = (paid == null ? BigDecimal.ZERO : paid).add(payment.getAmount());
		if (newTotal.compareTo(bill.getAmount()) == 0) {
			bill.setStatus(VendorBill.Status.PAID);
			vendorBillRepository.save(bill);
		}
		accountingService.postVendorPayment(payment.getAmount(), payment.getId());
		return payment;
	}

	private void checkOutstanding(BigDecimal amount, BigDecimal total, BigDecimal alreadyPaid) {
		if (amount == null || amount.signum() <= 0) {
			throw new BusinessException("Payment amount must be greater than zero");
		}
		BigDecimal remaining = total.subtract(alreadyPaid == null ? BigDecimal.ZERO : alreadyPaid);
		if (amount.compareTo(remaining) > 0) {
			throw new BusinessException("Payment cannot exceed the outstanding balance of " + remaining);
		}
	}
}