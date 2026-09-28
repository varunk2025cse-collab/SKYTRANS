package com.example.SKYTRANS;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.AnalyticAccount;
import com.example.SKYTRANS.model.Budget;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.model.Journal;
import com.example.SKYTRANS.model.JournalEntry;
import com.example.SKYTRANS.model.Payment;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.model.PurchaseOrder;
import com.example.SKYTRANS.model.SafetyConfiguration;
import com.example.SKYTRANS.model.SalesOrder;
import com.example.SKYTRANS.model.Telemetry;
import com.example.SKYTRANS.model.Terminal;
import com.example.SKYTRANS.model.TransitRoute;
import com.example.SKYTRANS.model.Trip;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.repository.AccountRepository;
import com.example.SKYTRANS.repository.AnalyticAccountRepository;
import com.example.SKYTRANS.repository.BudgetRepository;
import com.example.SKYTRANS.repository.CableCarRepository;
import com.example.SKYTRANS.repository.ContactRepository;
import com.example.SKYTRANS.repository.InvoiceRepository;
import com.example.SKYTRANS.repository.JournalEntryRepository;
import com.example.SKYTRANS.repository.JournalRepository;
import com.example.SKYTRANS.repository.PaymentRepository;
import com.example.SKYTRANS.repository.ProductRepository;
import com.example.SKYTRANS.repository.PurchaseOrderRepository;
import com.example.SKYTRANS.repository.SafetyConfigurationRepository;
import com.example.SKYTRANS.repository.SalesOrderRepository;
import com.example.SKYTRANS.repository.TerminalRepository;
import com.example.SKYTRANS.repository.TransitRouteRepository;
import com.example.SKYTRANS.repository.TripRepository;
import com.example.SKYTRANS.repository.VendorBillRepository;
import com.example.SKYTRANS.service.TelemetryService;

@Component
public class SampleDataLoader implements CommandLineRunner {
	private final ContactRepository contactRepository;
	private final ProductRepository productRepository;
	private final AccountRepository accountRepository;
	private final JournalRepository journalRepository;
	private final CableCarRepository cableCarRepository;
	private final TerminalRepository terminalRepository;
	private final TransitRouteRepository routeRepository;
	private final TripRepository tripRepository;
	private final SafetyConfigurationRepository configurationRepository;
	private final TelemetryService telemetryService;
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final VendorBillRepository vendorBillRepository;
	private final SalesOrderRepository salesOrderRepository;
	private final InvoiceRepository invoiceRepository;
	private final PaymentRepository paymentRepository;
	private final AnalyticAccountRepository analyticAccountRepository;
	private final BudgetRepository budgetRepository;
	private final JournalEntryRepository entryRepository;
	private final SampleBusinessFlowLoader businessFlowLoader;

	public SampleDataLoader(ContactRepository contactRepository, ProductRepository productRepository,
			AccountRepository accountRepository, JournalRepository journalRepository,
			CableCarRepository cableCarRepository, TerminalRepository terminalRepository,
			TransitRouteRepository routeRepository, TripRepository tripRepository,
			SafetyConfigurationRepository configurationRepository, TelemetryService telemetryService,
			PurchaseOrderRepository purchaseOrderRepository, VendorBillRepository vendorBillRepository,
			SalesOrderRepository salesOrderRepository, InvoiceRepository invoiceRepository,
			PaymentRepository paymentRepository, AnalyticAccountRepository analyticAccountRepository,
			BudgetRepository budgetRepository, JournalEntryRepository entryRepository,
			SampleBusinessFlowLoader businessFlowLoader) {
		this.contactRepository = contactRepository;
		this.productRepository = productRepository;
		this.accountRepository = accountRepository;
		this.journalRepository = journalRepository;
		this.cableCarRepository = cableCarRepository;
		this.terminalRepository = terminalRepository;
		this.routeRepository = routeRepository;
		this.tripRepository = tripRepository;
		this.configurationRepository = configurationRepository;
		this.telemetryService = telemetryService;
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.vendorBillRepository = vendorBillRepository;
		this.salesOrderRepository = salesOrderRepository;
		this.invoiceRepository = invoiceRepository;
		this.paymentRepository = paymentRepository;
		this.analyticAccountRepository = analyticAccountRepository;
		this.budgetRepository = budgetRepository;
		this.entryRepository = entryRepository;
		this.businessFlowLoader = businessFlowLoader;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (contactRepository.count() > 0) {
			return;
		}

		Contact customer = contactRepository.save(new Contact("Municipal Transit Authority",
				Contact.ContactType.CUSTOMER, "transit@authority.example", "555-0100", "Central Transit Office"));
		Contact vendor = contactRepository.save(new Contact("Cable Engineering Specialist",
				Contact.ContactType.VENDOR, "service@cableworks.example", "555-0120", "Mountain Line Depot"));

		Account bank = saveAccount("1000", "Cash/Bank", Account.AccountType.ASSET);
		saveAccount("1100", "Aerial Cable Line", Account.AccountType.ASSET);
		saveAccount("1150", "Gondola Fleet", Account.AccountType.ASSET);
		Account receivable = saveAccount("1200", "Customer Transit Receivables", Account.AccountType.ASSET);
		Account creditors = saveAccount("2000", "Cable Repair Vendor Creditors", Account.AccountType.LIABILITY);
		saveAccount("2010", "Power Utility Payables", Account.AccountType.LIABILITY);
		Account revenue = saveAccount("4000", "Municipal Transit Revenue", Account.AccountType.INCOME);
		saveAccount("4100", "Commercial Advertising Revenue", Account.AccountType.INCOME);
		Account motorExpense = saveAccount("5000", "Cable Motor Power Expenses", Account.AccountType.EXPENSE);
		Account inspectionExpense = saveAccount("5100", "Mechanical Inspection Costs", Account.AccountType.EXPENSE);

		// Each product is linked to the ledger account used when it is sold or bought
		Product transitProduct = saveProduct("Aerial Cable Passenger Transit",
				"Monthly passenger transport service", money("20.00"), revenue);
		Product inspectionProduct = saveProduct("Cable Tension Inspection",
				"Ultrasonic cable inspection", money("30000.00"), inspectionExpense);
		saveProduct("Motor Drive Overhaul", "Drive motor servicing", money("85000.00"), motorExpense);

		Journal salesJournal = journalRepository.save(new Journal("Sales Journal", Journal.JournalType.SALES));
		Journal purchaseJournal = journalRepository.save(new Journal("Purchase Journal", Journal.JournalType.PURCHASE));
		journalRepository.save(new Journal("Cash Journal", Journal.JournalType.CASH));
		journalRepository.save(new Journal("Bank Journal", Journal.JournalType.BANK));

		// Initial demo safety limits. After start-up they are read from the database only.
		SafetyConfiguration limits = new SafetyConfiguration();
		limits.setMaximumCableSpeed(new BigDecimal("60"));
		limits.setMinimumCableTension(new BigDecimal("40"));
		limits.setMaximumCableTension(new BigDecimal("100"));
		limits.setMaximumMotorPower(new BigDecimal("500"));
		limits.setMaximumPassengerCapacity(50);
		configurationRepository.save(limits);

		CableCar firstCar = cableCarRepository.save(new CableCar("CR-101", 40, new BigDecimal("55")));
		CableCar secondCar = cableCarRepository.save(new CableCar("CR-102", 50, new BigDecimal("60")));
		CableCar thirdCar = new CableCar("CR-103", 35, new BigDecimal("45"));
		thirdCar.setStatus(CableCar.Status.MAINTENANCE);
		cableCarRepository.save(thirdCar);

		Terminal north = terminalRepository.save(new Terminal("NTH", "North Terminal", "North Ridge"));
		Terminal valley = terminalRepository.save(new Terminal("VLY", "Valley Exchange", "River Valley"));
		Terminal summit = terminalRepository.save(new Terminal("SUM", "Summit Station", "Mountain Summit"));
		Terminal east = terminalRepository.save(new Terminal("EST", "East Tram Interchange", "East District"));

		TransitRoute routeOne = saveRoute("MNT-01", north, valley, "4.8", 12);
		TransitRoute routeTwo = saveRoute("MNT-02", valley, summit, "6.2", 18);
		TransitRoute routeThree = saveRoute("TRM-01", summit, east, "3.1", 9);

		LocalDateTime now = LocalDateTime.now();
		tripRepository.save(makeCompletedTrip(firstCar, routeOne, 20, now.withHour(8).withMinute(15)));
		tripRepository.save(makeCompletedTrip(secondCar, routeTwo, 25, now.withHour(10).withMinute(30)));
		tripRepository.save(makeCompletedTrip(firstCar, routeThree, 18, now.withHour(13).withMinute(0)));
		Trip scheduledTrip = new Trip();
		scheduledTrip.setCableCar(firstCar);
		scheduledTrip.setRoute(routeOne);
		scheduledTrip.setPassengerCount(12);
		tripRepository.save(scheduledTrip);

		telemetryService.receive(makeTelemetry(firstCar, 22, "67", "210", "34", "86", "31"));
		telemetryService.receive(makeTelemetry(secondCar, 60, "35", "550", "65", "58", "73"));

		PurchaseOrder order = new PurchaseOrder();
		order.setVendor(vendor);
		order.setProduct(inspectionProduct);
		order.setQuantity(1);
		order.setUnitPrice(money("30000.00"));
		order.setTotalAmount(money("30000.00"));
		order.setOrderDate(LocalDate.now().minusDays(5));
		order.setStatus(PurchaseOrder.Status.BILLED);
		PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

		VendorBill bill = new VendorBill();
		bill.setPurchaseOrder(savedOrder);
		bill.setVendor(vendor);
		bill.setAmount(money("30000.00"));
		bill.setBillDate(LocalDate.now().minusDays(2));
		bill.setStatus(VendorBill.Status.UNPAID);
		VendorBill savedBill = vendorBillRepository.save(bill);

		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setCustomer(customer);
		salesOrder.setProduct(transitProduct);
		salesOrder.setPassengerTrips(10000);
		salesOrder.setRatePerPassenger(money("20.00"));
		salesOrder.setTotalAmount(money("200000.00"));
		salesOrder.setOrderDate(LocalDate.now().minusDays(1));
		salesOrder.setStatus(SalesOrder.Status.INVOICED);
		SalesOrder savedSalesOrder = salesOrderRepository.save(salesOrder);

		Invoice invoice = new Invoice();
		invoice.setSalesOrder(savedSalesOrder);
		invoice.setCustomer(customer);
		invoice.setPassengerTrips(10000);
		invoice.setAmount(money("200000.00"));
		invoice.setInvoiceDate(LocalDate.now());
		invoice.setStatus(Invoice.Status.UNPAID);
		Invoice savedInvoice = invoiceRepository.save(invoice);

		Payment customerPayment = new Payment();
		customerPayment.setType(Payment.Type.CUSTOMER);
		customerPayment.setInvoice(savedInvoice);
		customerPayment.setAmount(money("50000.00"));
		customerPayment.setPaymentDate(LocalDate.now());
		paymentRepository.save(customerPayment);

		Payment vendorPayment = new Payment();
		vendorPayment.setType(Payment.Type.VENDOR);
		vendorPayment.setVendorBill(savedBill);
		vendorPayment.setAmount(money("10000.00"));
		vendorPayment.setPaymentDate(LocalDate.now());
		paymentRepository.save(vendorPayment);

		postEntry(LocalDate.now().minusDays(2), "Vendor inspection bill", purchaseJournal, inspectionExpense,
				creditors, money("30000.00"));
		postEntry(LocalDate.now().minusDays(1), "Monthly transit invoice", salesJournal, receivable,
				revenue, money("200000.00"));
		Journal bankJournal = journalRepository.findByType(Journal.JournalType.BANK).orElseThrow();
		postEntry(LocalDate.now(), "Customer partial payment", bankJournal, bank, receivable, money("50000.00"));
		postEntry(LocalDate.now(), "Vendor partial payment", bankJournal, creditors, bank, money("10000.00"));

		AnalyticAccount corridor = new AnalyticAccount();
		corridor.setName("Transit Corridor - Mountain Line Sector 1");
		corridor.setDescription("Mountain Line annual operating budget");
		corridor = analyticAccountRepository.save(corridor);
		Budget budget = new Budget();
		budget.setAnalyticAccount(corridor);
		budget.setBudgetYear(LocalDate.now().getYear());
		budget.setPlannedRevenue(money("300000.00"));
		budget.setElectricityBudget(money("40000.00"));
		budget.setMaintenanceBudget(money("60000.00"));
		budgetRepository.save(budget);

		// More customers, vendors, cars, trips, orders, invoices, bills, payments and budgets
		businessFlowLoader.load();
	}

	private Account saveAccount(String code, String name, Account.AccountType type) {
		return accountRepository.save(new Account(code, name, type, BigDecimal.ZERO));
	}

	private Product saveProduct(String name, String description, BigDecimal unitPrice, Account account) {
		Product product = new Product(name, description, "SERVICE", unitPrice);
		product.setAccount(account);
		return productRepository.save(product);
	}

	private TransitRoute saveRoute(String code, Terminal source, Terminal destination, String distance, int minutes) {
		TransitRoute route = new TransitRoute();
		route.setRouteCode(code);
		route.setSourceTerminal(source);
		route.setDestinationTerminal(destination);
		route.setDistance(new BigDecimal(distance));
		route.setEstimatedTime(minutes);
		route.setStatus("ACTIVE");
		return routeRepository.save(route);
	}

	private Trip makeCompletedTrip(CableCar cableCar, TransitRoute route, int passengers, LocalDateTime startTime) {
		Trip trip = new Trip();
		trip.setCableCar(cableCar);
		trip.setRoute(route);
		trip.setPassengerCount(passengers);
		trip.setStartTime(startTime);
		trip.setEndTime(startTime.plusMinutes(route.getEstimatedTime()));
		trip.setStatus(Trip.Status.COMPLETED);
		return trip;
	}

	private Telemetry makeTelemetry(CableCar cableCar, int passengers, String tension, String motorPower,
			String speed, String battery, String temperature) {
		Telemetry telemetry = new Telemetry();
		telemetry.setCableCar(cableCar);
		telemetry.setPassengerCount(passengers);
		telemetry.setCableTension(new BigDecimal(tension));
		telemetry.setMotorPowerUsage(new BigDecimal(motorPower));
		telemetry.setSpeed(new BigDecimal(speed));
		telemetry.setBatteryLevel(new BigDecimal(battery));
		telemetry.setTemperature(new BigDecimal(temperature));
		return telemetry;
	}

	private void postEntry(LocalDate date, String description, Journal journal, Account debit, Account credit,
			BigDecimal amount) {
		JournalEntry entry = new JournalEntry();
		entry.setDate(date);
		entry.setDescription(description);
		entry.setJournal(journal);
		entry.setDebitAccount(debit);
		entry.setCreditAccount(credit);
		entry.setAmount(amount);
		entryRepository.save(entry);
	}

	private BigDecimal money(String amount) { return new BigDecimal(amount); }
}