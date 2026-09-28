package com.example.SKYTRANS.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.model.JournalEntry;
import com.example.SKYTRANS.model.Payment;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.model.PurchaseOrder;
import com.example.SKYTRANS.model.SalesOrder;
import com.example.SKYTRANS.model.Telemetry;
import com.example.SKYTRANS.model.Terminal;
import com.example.SKYTRANS.model.TransitRoute;
import com.example.SKYTRANS.model.Trip;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.repository.AccountRepository;
import com.example.SKYTRANS.repository.AlertRepository;
import com.example.SKYTRANS.repository.AnalyticAccountRepository;
import com.example.SKYTRANS.repository.CableCarRepository;
import com.example.SKYTRANS.repository.ContactRepository;
import com.example.SKYTRANS.repository.InvoiceRepository;
import com.example.SKYTRANS.repository.JournalEntryRepository;
import com.example.SKYTRANS.repository.PaymentRepository;
import com.example.SKYTRANS.repository.ProductRepository;
import com.example.SKYTRANS.repository.PurchaseOrderRepository;
import com.example.SKYTRANS.repository.SalesOrderRepository;
import com.example.SKYTRANS.repository.TelemetryRepository;
import com.example.SKYTRANS.repository.TerminalRepository;
import com.example.SKYTRANS.repository.TransitRouteRepository;
import com.example.SKYTRANS.repository.TripRepository;
import com.example.SKYTRANS.repository.VendorBillRepository;
import com.example.SKYTRANS.service.PassengerReportService;
import com.example.SKYTRANS.service.ReportService;
import com.example.SKYTRANS.service.SafetyConfigurationService;

// Serves the single Thymeleaf page (index.html). The "page" parameter decides which section is shown.
// Forms on the page save data through the REST controllers (/api/...) using JavaScript.
@Controller
public class DashboardController {
	private static final Map<String, String> PAGE_TITLES = new LinkedHashMap<>();
	static {
		PAGE_TITLES.put("dashboard", "Operations Dashboard");
		PAGE_TITLES.put("cable-cars", "Cable Cars");
		PAGE_TITLES.put("terminals", "Terminals");
		PAGE_TITLES.put("routes", "Routes");
		PAGE_TITLES.put("trips", "Trips");
		PAGE_TITLES.put("telemetry", "Telemetry & Safety");
		PAGE_TITLES.put("contacts", "Contacts");
		PAGE_TITLES.put("products", "Products");
		PAGE_TITLES.put("accounts", "Chart of Accounts");
		PAGE_TITLES.put("accounting", "Accounting Journal");
		PAGE_TITLES.put("purchase-orders", "Purchase Orders");
		PAGE_TITLES.put("vendor-bills", "Vendor Bills");
		PAGE_TITLES.put("sales-orders", "Sales Orders");
		PAGE_TITLES.put("invoices", "Customer Invoices");
		PAGE_TITLES.put("payments", "Payments");
		PAGE_TITLES.put("budgets", "Budgets");
		PAGE_TITLES.put("reports", "Financial Reports");
	}

	private final CableCarRepository cableCarRepository;
	private final TripRepository tripRepository;
	private final AlertRepository alertRepository;
	private final ContactRepository contactRepository;
	private final ProductRepository productRepository;
	private final AccountRepository accountRepository;
	private final TerminalRepository terminalRepository;
	private final TransitRouteRepository routeRepository;
	private final TelemetryRepository telemetryRepository;
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final VendorBillRepository vendorBillRepository;
	private final SalesOrderRepository salesOrderRepository;
	private final InvoiceRepository invoiceRepository;
	private final PaymentRepository paymentRepository;
	private final AnalyticAccountRepository analyticAccountRepository;
	private final JournalEntryRepository journalEntryRepository;
	private final ReportService reportService;
	private final PassengerReportService passengerReportService;
	private final SafetyConfigurationService safetyConfigurationService;

	public DashboardController(CableCarRepository cableCarRepository, TripRepository tripRepository,
			AlertRepository alertRepository, ContactRepository contactRepository, ProductRepository productRepository,
			AccountRepository accountRepository, TerminalRepository terminalRepository,
			TransitRouteRepository routeRepository, TelemetryRepository telemetryRepository,
			PurchaseOrderRepository purchaseOrderRepository, VendorBillRepository vendorBillRepository,
			SalesOrderRepository salesOrderRepository, InvoiceRepository invoiceRepository,
			PaymentRepository paymentRepository, AnalyticAccountRepository analyticAccountRepository,
			JournalEntryRepository journalEntryRepository, ReportService reportService,
			PassengerReportService passengerReportService, SafetyConfigurationService safetyConfigurationService) {
		this.cableCarRepository = cableCarRepository;
		this.tripRepository = tripRepository;
		this.alertRepository = alertRepository;
		this.contactRepository = contactRepository;
		this.productRepository = productRepository;
		this.accountRepository = accountRepository;
		this.terminalRepository = terminalRepository;
		this.routeRepository = routeRepository;
		this.telemetryRepository = telemetryRepository;
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.vendorBillRepository = vendorBillRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.invoiceRepository = invoiceRepository;
		this.paymentRepository = paymentRepository;
		this.analyticAccountRepository = analyticAccountRepository;
		this.journalEntryRepository = journalEntryRepository;
		this.reportService = reportService;
		this.passengerReportService = passengerReportService;
		this.safetyConfigurationService = safetyConfigurationService;
	}

	@GetMapping("/")
	public String showPage(@RequestParam(defaultValue = "dashboard") String page, Model model) {
		if (!PAGE_TITLES.containsKey(page)) {
			page = "dashboard";
		}
		model.addAttribute("page", page);
		model.addAttribute("pageTitle", PAGE_TITLES.get(page));
		if ("dashboard".equals(page)) {
			addDashboardData(model);
		} else if ("reports".equals(page)) {
			model.addAttribute("balanceSheet", reportService.getBalanceSheet());
			model.addAttribute("profitLoss", reportService.getProfitAndLoss());
			model.addAttribute("budgetRows", reportService.getBudgetReport());
		} else {
			addFormData(page, model);
			addTableData(page, model);
		}
		return "index";
	}

	private void addDashboardData(Model model) {
		long activeTrips = 0;
		for (Trip trip : tripRepository.findAll()) {
			if (trip.getStatus() == Trip.Status.RUNNING) {
				activeTrips++;
			}
		}
		Map<String, BigDecimal> monthTotals = reportService.getCurrentMonthTotals();
		model.addAttribute("cableCarCount", cableCarRepository.count());
		model.addAttribute("activeTripCount", activeTrips);
		model.addAttribute("dailyPassengers", passengerReportService.getDailyPassengerCount(LocalDate.now()));
		model.addAttribute("alertRows", alertRepository.findByResolvedFalseOrderByCreatedAtDesc());
		model.addAttribute("monthlyRevenue", monthTotals.get("revenue"));
		model.addAttribute("monthlyExpenses", monthTotals.get("expenses"));
		model.addAttribute("cableCarRows", cableCarRepository.findAll());
		model.addAttribute("recentTelemetry", telemetryRepository.findTop5ByOrderByTimestampDesc());
		model.addAttribute("profitLoss", reportService.getProfitAndLoss());
		model.addAttribute("balanceSheet", reportService.getBalanceSheet());
	}

	// Drop-down options for the form shown on each page
	private void addFormData(String page, Model model) {
		if ("routes".equals(page)) {
			model.addAttribute("terminals", terminalRepository.findAll());
		} else if ("trips".equals(page)) {
			model.addAttribute("cableCars", cableCarRepository.findAll());
			model.addAttribute("routes", routeRepository.findAll());
		} else if ("telemetry".equals(page)) {
			model.addAttribute("cableCars", cableCarRepository.findAll());
			model.addAttribute("safety", safetyConfigurationService.getCurrent());
		} else if ("products".equals(page)) {
			model.addAttribute("accounts", accountRepository.findAll());
		} else if ("purchase-orders".equals(page)) {
			model.addAttribute("vendors", findContacts(Contact.ContactType.VENDOR));
			model.addAttribute("products", productRepository.findAll());
		} else if ("vendor-bills".equals(page)) {
			List<PurchaseOrder> approvedOrders = new ArrayList<>();
			for (PurchaseOrder order : purchaseOrderRepository.findAll()) {
				if (order.getStatus() == PurchaseOrder.Status.APPROVED) approvedOrders.add(order);
			}
			model.addAttribute("approvedOrders", approvedOrders);
		} else if ("sales-orders".equals(page)) {
			model.addAttribute("customers", findContacts(Contact.ContactType.CUSTOMER));
			model.addAttribute("products", productRepository.findAll());
		} else if ("invoices".equals(page)) {
			List<SalesOrder> openOrders = new ArrayList<>();
			for (SalesOrder order : salesOrderRepository.findAll()) {
				if (order.getStatus() == SalesOrder.Status.CREATED) openOrders.add(order);
			}
			model.addAttribute("openSalesOrders", openOrders);
		} else if ("payments".equals(page)) {
			addPaymentFormData(model);
		} else if ("budgets".equals(page)) {
			model.addAttribute("corridors", analyticAccountRepository.findAll());
			model.addAttribute("currentYear", LocalDate.now().getYear());
		}
	}

	private void addPaymentFormData(Model model) {
		List<Invoice> unpaidInvoices = new ArrayList<>();
		Map<Long, BigDecimal> invoiceOutstanding = new HashMap<>();
		for (Invoice invoice : invoiceRepository.findAll()) {
			if (invoice.getStatus() == Invoice.Status.UNPAID) {
				unpaidInvoices.add(invoice);
				invoiceOutstanding.put(invoice.getId(), invoiceOutstanding(invoice));
			}
		}
		List<VendorBill> unpaidBills = new ArrayList<>();
		Map<Long, BigDecimal> billOutstanding = new HashMap<>();
		for (VendorBill bill : vendorBillRepository.findAll()) {
			if (bill.getStatus() == VendorBill.Status.UNPAID) {
				unpaidBills.add(bill);
				billOutstanding.put(bill.getId(), billOutstanding(bill));
			}
		}
		model.addAttribute("unpaidInvoices", unpaidInvoices);
		model.addAttribute("invoiceOutstanding", invoiceOutstanding);
		model.addAttribute("unpaidBills", unpaidBills);
		model.addAttribute("billOutstanding", billOutstanding);
	}

	private void addTableData(String page, Model model) {
		List<String> headers = new ArrayList<>();
		List<TableRow> rows = new ArrayList<>();
		if ("cable-cars".equals(page)) {
			headers.addAll(Arrays.asList("Vehicle", "Capacity", "Max Speed", "Speed", "Battery %", "Motor Power", "Status"));
			for (CableCar car : cableCarRepository.findAll()) addRow(rows, car.getVehicleNumber(), car.getCapacity(), car.getMaximumSpeed(), car.getCurrentSpeed(), car.getBatteryLevel(), car.getMotorPower(), car.getStatus());
		} else if ("terminals".equals(page)) {
			headers.addAll(Arrays.asList("Code", "Name", "Location", "Status"));
			for (Terminal terminal : terminalRepository.findAll()) addRow(rows, terminal.getTerminalCode(), terminal.getName(), terminal.getLocation(), terminal.getStatus());
		} else if ("routes".equals(page)) {
			headers.addAll(Arrays.asList("Code", "Source", "Destination", "Distance (km)", "Minutes", "Status"));
			for (TransitRoute route : routeRepository.findAll()) addRow(rows, route.getRouteCode(), route.getSourceTerminal().getName(), route.getDestinationTerminal().getName(), route.getDistance(), route.getEstimatedTime(), route.getStatus());
		} else if ("trips".equals(page)) {
			headers.addAll(Arrays.asList("Trip", "Car", "Route", "Passengers", "Start", "End", "Status"));
			for (Trip trip : tripRepository.findAll()) {
				TableRow row = addRow(rows, "T-" + trip.getId(), trip.getCableCar().getVehicleNumber(), trip.getRoute().getRouteCode(), trip.getPassengerCount(), trip.getStartTime(), trip.getEndTime(), trip.getStatus());
				if (trip.getStatus() == Trip.Status.SCHEDULED) row.setAction("Start trip", "/api/trips/" + trip.getId() + "/start");
				if (trip.getStatus() == Trip.Status.RUNNING) row.setAction("Complete trip", "/api/trips/" + trip.getId() + "/complete");
			}
		} else if ("telemetry".equals(page)) {
			headers.addAll(Arrays.asList("Car", "Passengers", "Tension", "Motor Power", "Speed", "Battery %", "Temperature", "Timestamp"));
			for (Telemetry item : telemetryRepository.findAll()) addRow(rows, item.getCableCar().getVehicleNumber(), item.getPassengerCount(), item.getCableTension(), item.getMotorPowerUsage(), item.getSpeed(), item.getBatteryLevel(), item.getTemperature(), item.getTimestamp());
		} else if ("contacts".equals(page)) {
			headers.addAll(Arrays.asList("Name", "Type", "Email", "Phone", "Address"));
			for (Contact contact : contactRepository.findAll()) addRow(rows, contact.getName(), contact.getType(), contact.getEmail(), contact.getPhone(), contact.getAddress());
		} else if ("products".equals(page)) {
			headers.addAll(Arrays.asList("Name", "Type", "Unit Price", "Ledger Account", "Description"));
			for (Product product : productRepository.findAll()) {
				String account = product.getAccount() == null ? "Not set" : product.getAccount().getAccountCode() + " " + product.getAccount().getAccountName();
				addRow(rows, product.getName(), product.getType(), product.getUnitPrice(), account, product.getDescription());
			}
		} else if ("accounts".equals(page)) {
			headers.addAll(Arrays.asList("Code", "Account", "Type", "Opening Balance"));
			for (Account account : accountRepository.findAll()) addRow(rows, account.getAccountCode(), account.getAccountName(), account.getAccountType(), account.getOpeningBalance());
		} else if ("purchase-orders".equals(page)) {
			headers.addAll(Arrays.asList("PO", "Vendor", "Product", "Quantity", "Unit Price", "Total", "Date", "Status"));
			for (PurchaseOrder order : purchaseOrderRepository.findAll()) {
				TableRow row = addRow(rows, "PO-" + order.getId(), order.getVendor().getName(), order.getProduct().getName(), order.getQuantity(), order.getUnitPrice(), order.getTotalAmount(), order.getOrderDate(), order.getStatus());
				if (order.getStatus() == PurchaseOrder.Status.CREATED) row.setAction("Approve", "/api/purchase-orders/" + order.getId() + "/approve");
			}
		} else if ("vendor-bills".equals(page)) {
			headers.addAll(Arrays.asList("Bill", "Vendor", "Purchase Order", "Amount", "Paid", "Outstanding", "Date", "Status"));
			for (VendorBill bill : vendorBillRepository.findAll()) {
				BigDecimal outstanding = billOutstanding(bill);
				addRow(rows, "BILL-" + bill.getId(), bill.getVendor().getName(), "PO-" + bill.getPurchaseOrder().getId(), bill.getAmount(), bill.getAmount().subtract(outstanding), outstanding, bill.getBillDate(), bill.getStatus());
			}
		} else if ("sales-orders".equals(page)) {
			headers.addAll(Arrays.asList("SO", "Customer", "Product", "Passenger Trips", "Rate", "Total", "Date", "Status"));
			for (SalesOrder order : salesOrderRepository.findAll()) addRow(rows, "SO-" + order.getId(), order.getCustomer().getName(), order.getProduct().getName(), order.getPassengerTrips(), order.getRatePerPassenger(), order.getTotalAmount(), order.getOrderDate(), order.getStatus());
		} else if ("invoices".equals(page)) {
			headers.addAll(Arrays.asList("Invoice", "Customer", "Passenger Trips", "Rate", "Total", "Paid", "Outstanding", "Date", "Status"));
			for (Invoice invoice : invoiceRepository.findAll()) {
				BigDecimal outstanding = invoiceOutstanding(invoice);
				addRow(rows, "INV-" + invoice.getId(), invoice.getCustomer().getName(), invoice.getPassengerTrips(), invoice.getSalesOrder().getRatePerPassenger(), invoice.getAmount(), invoice.getAmount().subtract(outstanding), outstanding, invoice.getInvoiceDate(), invoice.getStatus());
			}
		} else if ("payments".equals(page)) {
			headers.addAll(Arrays.asList("Payment", "Type", "Invoice / Bill", "Amount", "Date"));
			for (Payment payment : paymentRepository.findAll()) {
				String reference = payment.getInvoice() != null ? "INV-" + payment.getInvoice().getId() : "BILL-" + payment.getVendorBill().getId();
				addRow(rows, "PAY-" + payment.getId(), payment.getType(), reference, payment.getAmount(), payment.getPaymentDate());
			}
		} else if ("budgets".equals(page)) {
			headers.addAll(Arrays.asList("Corridor", "Year", "Planned Revenue", "Actual Revenue", "Revenue Variance", "Electricity Plan", "Electricity Actual", "Electricity Variance", "Maintenance Plan", "Maintenance Actual", "Maintenance Variance"));
			for (Map<String, Object> item : reportService.getBudgetReport()) {
				addRow(rows, item.get("transitCorridor"), item.get("budgetYear"), item.get("plannedRevenue"), item.get("actualRevenue"), item.get("revenueVariance"), item.get("electricityBudget"), item.get("actualElectricityExpense"), item.get("electricityVariance"), item.get("maintenanceBudget"), item.get("actualMaintenanceExpense"), item.get("maintenanceVariance"));
			}
		} else if ("accounting".equals(page)) {
			headers.addAll(Arrays.asList("Date", "Description", "Debit Account", "Credit Account", "Journal", "Amount"));
			for (JournalEntry entry : journalEntryRepository.findAll()) addRow(rows, entry.getDate(), entry.getDescription(), entry.getDebitAccount().getAccountName(), entry.getCreditAccount().getAccountName(), entry.getJournal().getName(), entry.getAmount());
		}
		model.addAttribute("headers", headers);
		model.addAttribute("rows", rows);
	}

	private TableRow addRow(List<TableRow> rows, Object... values) {
		List<String> cells = new ArrayList<>();
		for (Object value : values) {
			cells.add(value == null ? "-" : value.toString());
		}
		TableRow row = new TableRow(cells);
		rows.add(row);
		return row;
	}

	private List<Contact> findContacts(Contact.ContactType type) {
		List<Contact> contacts = new ArrayList<>();
		for (Contact contact : contactRepository.findAll()) {
			if (contact.getType() == type) contacts.add(contact);
		}
		return contacts;
	}

	private BigDecimal invoiceOutstanding(Invoice invoice) {
		BigDecimal paid = paymentRepository.sumByInvoiceId(invoice.getId());
		return invoice.getAmount().subtract(paid == null ? BigDecimal.ZERO : paid);
	}

	private BigDecimal billOutstanding(VendorBill bill) {
		BigDecimal paid = paymentRepository.sumByVendorBillId(bill.getId());
		return bill.getAmount().subtract(paid == null ? BigDecimal.ZERO : paid);
	}
}
