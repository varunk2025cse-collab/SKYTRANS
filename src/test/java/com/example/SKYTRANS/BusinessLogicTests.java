package com.example.SKYTRANS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.example.SKYTRANS.exception.BusinessException;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.Alert;
import com.example.SKYTRANS.model.Budget;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.model.Invoice;
import com.example.SKYTRANS.model.Journal;
import com.example.SKYTRANS.model.JournalEntry;
import com.example.SKYTRANS.model.Payment;
import com.example.SKYTRANS.model.Product;
import com.example.SKYTRANS.model.PurchaseOrder;
import com.example.SKYTRANS.model.SalesOrder;
import com.example.SKYTRANS.model.SafetyConfiguration;
import com.example.SKYTRANS.model.Telemetry;
import com.example.SKYTRANS.model.TransitRoute;
import com.example.SKYTRANS.model.Trip;
import com.example.SKYTRANS.model.VendorBill;
import com.example.SKYTRANS.repository.AccountRepository;
import com.example.SKYTRANS.repository.AlertRepository;
import com.example.SKYTRANS.repository.CableCarRepository;
import com.example.SKYTRANS.repository.ContactRepository;
import com.example.SKYTRANS.repository.InvoiceRepository;
import com.example.SKYTRANS.repository.JournalEntryRepository;
import com.example.SKYTRANS.repository.PaymentRepository;
import com.example.SKYTRANS.repository.ProductRepository;
import com.example.SKYTRANS.repository.TransitRouteRepository;
import com.example.SKYTRANS.repository.VendorBillRepository;
import com.example.SKYTRANS.service.AccountingService;
import com.example.SKYTRANS.service.BudgetService;
import com.example.SKYTRANS.service.CableCarService;
import com.example.SKYTRANS.service.ContactService;
import com.example.SKYTRANS.service.InvoiceService;
import com.example.SKYTRANS.service.PassengerReportService;
import com.example.SKYTRANS.service.PaymentService;
import com.example.SKYTRANS.service.ProductService;
import com.example.SKYTRANS.service.PurchaseOrderService;
import com.example.SKYTRANS.service.ReportService;
import com.example.SKYTRANS.service.SafetyConfigurationService;
import com.example.SKYTRANS.service.SalesOrderService;
import com.example.SKYTRANS.service.TelemetryService;
import com.example.SKYTRANS.service.TripService;
import com.example.SKYTRANS.service.VendorBillService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BusinessLogicTests {
	@Autowired private ContactRepository contactRepository;
	@Autowired private ProductRepository productRepository;
	@Autowired private CableCarRepository cableCarRepository;
	@Autowired private TransitRouteRepository routeRepository;
	@Autowired private AlertRepository alertRepository;
	@Autowired private InvoiceRepository invoiceRepository;
	@Autowired private VendorBillRepository vendorBillRepository;
	@Autowired private PaymentRepository paymentRepository;
	@Autowired private AccountRepository accountRepository;
	@Autowired private JournalEntryRepository entryRepository;
	@Autowired private ContactService contactService;
	@Autowired private ProductService productService;
	@Autowired private CableCarService cableCarService;
	@Autowired private TripService tripService;
	@Autowired private TelemetryService telemetryService;
	@Autowired private PurchaseOrderService purchaseOrderService;
	@Autowired private VendorBillService vendorBillService;
	@Autowired private SalesOrderService salesOrderService;
	@Autowired private InvoiceService invoiceService;
	@Autowired private PaymentService paymentService;
	@Autowired private AccountingService accountingService;
	@Autowired private PassengerReportService passengerReportService;
	@Autowired private ReportService reportService;
	@Autowired private SafetyConfigurationService safetyConfigurationService;
	@Autowired private BudgetService budgetService;

	@Test
	void createsContactAndProductRecords() {
		Contact contact = contactService.save(new Contact("Test Transit Board", Contact.ContactType.CUSTOMER,
				"test@example.com", "555-0199", "Test address"));
		Product product = new Product("Test Transit Service", "Test service", "SERVICE", money("12.50"));
		product.setAccount(accountRepository.findByAccountCode("4000").orElseThrow());
		product = productService.save(product);

		assertNotNull(contact.getId());
		assertNotNull(product.getId());
		Product withoutAccount = new Product("No Account Service", "Missing ledger account", "SERVICE", money("5.00"));
		assertThrows(BusinessException.class, () -> productService.save(withoutAccount));
	}

	@Test
	void createsCableCarRecord() {
		CableCar cableCar = cableCarService.save(new CableCar("TEST-CAR-1", 25, money("45.0")));
		assertNotNull(cableCar.getId());
		assertEquals(CableCar.Status.READY, cableCar.getStatus());
	}

	@Test
	void rejectsTripsAboveVehicleCapacityAndStartsValidTrips() {
		CableCar car = cableCarService.save(new CableCar("TEST-CAR-2", 10, money("45.0")));
		TransitRoute route = routeRepository.findAll().get(0);
		Trip tooLarge = new Trip();
		tooLarge.setCableCar(car);
		tooLarge.setRoute(route);
		tooLarge.setPassengerCount(11);
		assertThrows(BusinessException.class, () -> tripService.create(tooLarge));

		Trip validTrip = new Trip();
		validTrip.setCableCar(car);
		validTrip.setRoute(route);
		validTrip.setPassengerCount(8);
		Trip savedTrip = tripService.create(validTrip);
		assertEquals(Trip.Status.SCHEDULED, savedTrip.getStatus());
		assertEquals(Trip.Status.RUNNING, tripService.start(savedTrip.getId()).getStatus());
		assertEquals(Trip.Status.COMPLETED, tripService.complete(savedTrip.getId()).getStatus());
	}

	@Test
	void telemetryUpdatesCarAndCreatesSafetyAlerts() {
		CableCar car = cableCarService.save(new CableCar("TEST-CAR-3", 20, money("50.0")));
		Telemetry telemetry = new Telemetry();
		telemetry.setCableCar(car);
		telemetry.setPassengerCount(21);
		telemetry.setCableTension(money("30"));
		telemetry.setMotorPowerUsage(money("600"));
		telemetry.setSpeed(money("70"));
		telemetry.setBatteryLevel(money("45"));
		telemetry.setTemperature(money("72"));

		Telemetry saved = telemetryService.receive(telemetry);

		assertNotNull(saved.getId());
		assertEquals(money("70"), cableCarRepository.findById(car.getId()).orElseThrow().getCurrentSpeed());
		List<Alert> alerts = alertRepository.findByResolvedFalseOrderByCreatedAtDesc();
		assertTrue(alerts.stream().anyMatch(alert -> alert.getCableCar().getId().equals(car.getId())
				&& alert.getType() == Alert.AlertType.LOW_CABLE_TENSION));
		assertTrue(alerts.stream().anyMatch(alert -> alert.getCableCar().getId().equals(car.getId())
				&& alert.getType() == Alert.AlertType.OVER_CAPACITY));
	}

	@Test
	void calculatesInvoiceAndPostsBalancedJournalEntry() {
		SalesOrder order = createSalesOrder(10, "20.00");
		assertEquals(money("200.00"), order.getTotalAmount());
		Invoice invoice = createInvoice(order);
		assertEquals(order.getTotalAmount(), invoice.getAmount());
		assertTrue(entryRepository.findAll().stream().anyMatch(entry -> entry.getDescription().contains("Customer invoice")
				&& entry.getAmount().compareTo(invoice.getAmount()) == 0));
	}

	@Test
	void calculatesVendorBillFromPurchaseOrder() {
		PurchaseOrder order = createPurchaseOrder(2, "15000.00");
		assertEquals(money("30000.00"), order.getTotalAmount());
		purchaseOrderService.approve(order.getId());
		VendorBill billRequest = new VendorBill();
		billRequest.setPurchaseOrder(order);
		VendorBill bill = vendorBillService.create(billRequest);
		assertEquals(order.getTotalAmount(), bill.getAmount());
		assertEquals(VendorBill.Status.UNPAID, bill.getStatus());
	}

	@Test
	void acceptsPartialCustomerPaymentsAndBlocksOverpayment() {
		Invoice invoice = createInvoice(createSalesOrder(10, "20.00"));
		assertEquals(money("200.00"), invoice.getAmount());
		paymentService.receiveCustomerPayment(customerPayment(invoice, "50.00"));
		assertThrows(BusinessException.class,
				() -> paymentService.receiveCustomerPayment(customerPayment(invoice, "151.00")));
		paymentService.receiveCustomerPayment(customerPayment(invoice, "150.00"));
		assertEquals(Invoice.Status.PAID, invoiceRepository.findById(invoice.getId()).orElseThrow().getStatus());
	}

	@Test
	void acceptsVendorPaymentsAndBlocksAmountsAboveOutstandingBill() {
		PurchaseOrder order = createPurchaseOrder(1, "1000.00");
		purchaseOrderService.approve(order.getId());
		VendorBill request = new VendorBill();
		request.setPurchaseOrder(order);
		VendorBill bill = vendorBillService.create(request);
		paymentService.payVendor(vendorPayment(bill, "400.00"));
		assertThrows(BusinessException.class, () -> paymentService.payVendor(vendorPayment(bill, "601.00")));
		paymentService.payVendor(vendorPayment(bill, "600.00"));
		assertEquals(VendorBill.Status.PAID, vendorBillRepository.findById(bill.getId()).orElseThrow().getStatus());
	}

	@Test
	void journalEntryUsesEqualDebitAndCreditAmounts() {
		Account bank = accountRepository.findByAccountCode("1000").orElseThrow();
		Account receivable = accountRepository.findByAccountCode("1200").orElseThrow();
		JournalEntry entry = accountingService.createEntry(LocalDate.now(), "Balance check", Journal.JournalType.BANK,
				bank, receivable, money("125.00"));
		// One amount is used for both sides, so debit always equals credit
		assertEquals("1000", entry.getDebitAccount().getAccountCode());
		assertEquals("1200", entry.getCreditAccount().getAccountCode());
		assertEquals(money("125.00"), entry.getAmount());
		assertThrows(BusinessException.class, () -> accountingService.createEntry(LocalDate.now(), "Invalid amount",
				Journal.JournalType.BANK, bank, receivable, money("0.00")));
		assertThrows(BusinessException.class, () -> accountingService.createEntry(LocalDate.now(), "Same account",
				Journal.JournalType.BANK, bank, bank, money("10.00")));
	}

	@Test
	void safetyLimitsAreReadFromTheDatabaseConfiguration() {
		CableCar car = cableCarService.save(new CableCar("TEST-CAR-4", 40, money("80.0")));
		TransitRoute route = routeRepository.findAll().get(0);

		// Seed limit is 50 passengers, so 35 is allowed on a 40-seat car
		Trip allowed = new Trip();
		allowed.setCableCar(car);
		allowed.setRoute(route);
		allowed.setPassengerCount(35);
		assertNotNull(tripService.create(allowed).getId());

		// Lower the configured limit: the same trip must now be rejected
		SafetyConfiguration limits = safetyConfigurationService.getCurrent();
		limits.setMaximumPassengerCapacity(30);
		safetyConfigurationService.update(limits);
		Trip rejected = new Trip();
		rejected.setCableCar(car);
		rejected.setRoute(route);
		rejected.setPassengerCount(35);
		assertThrows(BusinessException.class, () -> tripService.create(rejected));

		// Speed 55 is fine with the seed limit of 60, but not after the limit is lowered to 50
		telemetryService.receive(telemetry(car, 10, "60", "200", "55"));
		assertEquals(0, countAlerts(car, Alert.AlertType.OVERSPEED));
		limits.setMaximumCableSpeed(money("50"));
		safetyConfigurationService.update(limits);
		telemetryService.receive(telemetry(car, 10, "60", "200", "55"));
		assertEquals(1, countAlerts(car, Alert.AlertType.OVERSPEED));
	}

	@Test
	void validTelemetryCreatesNoAlertsAndLimitBreachesCreateEachAlertType() {
		CableCar car = cableCarService.save(new CableCar("TEST-CAR-5", 40, money("60.0")));
		telemetryService.receive(telemetry(car, 20, "70", "300", "40"));
		assertEquals(0, alertRepository.findAll().stream().filter(a -> a.getCableCar().getId().equals(car.getId())).count());

		telemetryService.receive(telemetry(car, 20, "120", "300", "40"));
		assertEquals(1, countAlerts(car, Alert.AlertType.HIGH_CABLE_TENSION));
		telemetryService.receive(telemetry(car, 20, "70", "650", "40"));
		assertEquals(1, countAlerts(car, Alert.AlertType.HIGH_MOTOR_POWER));
		telemetryService.receive(telemetry(car, 41, "70", "300", "40"));
		assertEquals(1, countAlerts(car, Alert.AlertType.OVER_CAPACITY));
	}

	@Test
	void tripStateRulesAreEnforced() {
		CableCar car = cableCarService.save(new CableCar("TEST-CAR-6", 20, money("45.0")));
		Trip trip = new Trip();
		trip.setCableCar(car);
		trip.setRoute(routeRepository.findAll().get(0));
		trip.setPassengerCount(5);
		Trip saved = tripService.create(trip);

		assertThrows(BusinessException.class, () -> tripService.complete(saved.getId()));
		tripService.start(saved.getId());
		assertEquals(CableCar.Status.RUNNING, cableCarRepository.findById(car.getId()).orElseThrow().getStatus());
		assertThrows(BusinessException.class, () -> tripService.start(saved.getId()));
		tripService.complete(saved.getId());
		assertEquals(CableCar.Status.READY, cableCarRepository.findById(car.getId()).orElseThrow().getStatus());
		assertThrows(BusinessException.class, () -> tripService.start(saved.getId()));
	}

	@Test
	void vendorBillIsPostedToTheProductLedgerAccountNotDecidedByName() {
		// Rename the inspection product: its accounting must not change
		Product inspection = findProduct("Cable Tension Inspection");
		inspection.setName("Annual Rope Check");
		productRepository.save(inspection);
		VendorBill inspectionBill = approveAndBill(createPurchaseOrder(inspection, 1, "500.00"));
		JournalEntry inspectionEntry = findEntry("Vendor bill " + inspectionBill.getId());
		assertEquals("5100", inspectionEntry.getDebitAccount().getAccountCode());
		assertEquals("2000", inspectionEntry.getCreditAccount().getAccountCode());

		// A product with "inspection" in its name but linked to 5000 goes to 5000
		Product misleading = new Product("Inspection Lamp Electricity", "Power supply", "SERVICE", money("100.00"));
		misleading.setAccount(accountRepository.findByAccountCode("5000").orElseThrow());
		misleading = productService.save(misleading);
		VendorBill powerBill = approveAndBill(createPurchaseOrder(misleading, 2, "100.00"));
		assertEquals("5000", findEntry("Vendor bill " + powerBill.getId()).getDebitAccount().getAccountCode());
	}

	@Test
	void balanceSheetBalancesAfterSalesPurchasesAndPayments() {
		Invoice invoice = createInvoice(createSalesOrder(100, "20.00"));
		paymentService.receiveCustomerPayment(customerPayment(invoice, "500.00"));
		VendorBill bill = approveAndBill(createPurchaseOrder(findProduct("Cable Tension Inspection"), 1, "800.00"));
		paymentService.payVendor(vendorPayment(bill, "300.00"));

		Map<String, Object> balanceSheet = reportService.getBalanceSheet();
		BigDecimal totalAssets = (BigDecimal) balanceSheet.get("totalAssets");
		BigDecimal liabilitiesAndEquity = (BigDecimal) balanceSheet.get("totalLiabilitiesAndEquity");
		assertEquals(0, totalAssets.compareTo(liabilitiesAndEquity));
		// Sample assets 356000 + invoice 2000 - vendor payment 300
		assertEquals(0, totalAssets.compareTo(money("357700.00")));
		// Sample creditors 120000 + bill 800 - payment 300
		assertEquals(0, ((BigDecimal) balanceSheet.get("totalLiabilities")).compareTo(money("120500.00")));
	}

	@Test
	void budgetReportUsesConfiguredExpenseAccountsAndRejectsDuplicates() {
		Product overhaul = findProduct("Motor Drive Overhaul");
		approveAndBill(createPurchaseOrder(overhaul, 1, "1500.00"));
		Map<String, Object> row = reportService.getBudgetReport().get(0);
		// Sample electricity bill 12000 + this bill 1500
		assertEquals(0, ((BigDecimal) row.get("actualElectricityExpense")).compareTo(money("13500.00")));
		assertEquals(0, ((BigDecimal) row.get("electricityVariance")).compareTo(money("-26500.00")));
		assertEquals(0, ((BigDecimal) row.get("actualMaintenanceExpense")).compareTo(money("30000.00")));
		assertEquals(0, ((BigDecimal) row.get("maintenanceVariance")).compareTo(money("-30000.00")));

		Budget duplicate = new Budget();
		duplicate.setAnalyticAccount(budgetService.findAllAnalyticAccounts().get(0));
		duplicate.setBudgetYear(LocalDate.now().getYear());
		duplicate.setPlannedRevenue(money("1"));
		duplicate.setElectricityBudget(money("1"));
		duplicate.setMaintenanceBudget(money("1"));
		assertThrows(BusinessException.class, () -> budgetService.create(duplicate));
	}

	@Test
	void calculatesPassengerVolumeAndBudgetVariances() {
		// Completed trips today: 20 + 25 + 18 (basic sample) + 30 + 42 (business-flow sample)
		assertEquals(135L, passengerReportService.getDailyPassengerCount(LocalDate.now()));
		List<Map<String, Object>> rows = reportService.getBudgetReport();
		assertEquals(3, rows.size());
		// Mountain Line: actual revenue 278000 - planned 300000; electricity 12000 - 40000
		assertEquals(0, ((BigDecimal) rows.get(0).get("revenueVariance")).compareTo(money("-22000.00")));
		assertEquals(0, ((BigDecimal) rows.get(0).get("electricityVariance")).compareTo(money("-28000.00")));
	}

	@Test
	void calculatesProfitAndLossFromPostedLedgerEntries() {
		Map<String, Object> report = reportService.getProfitAndLoss();
		// Income: 200000 + day passes 28000 + Metro Rail 50000. Expenses: 30000 + electricity 12000
		assertEquals(0, ((BigDecimal) report.get("totalIncome")).compareTo(money("278000.00")));
		assertEquals(0, ((BigDecimal) report.get("totalExpenses")).compareTo(money("42000.00")));
		assertEquals(0, ((BigDecimal) report.get("netProfit")).compareTo(money("236000.00")));
	}

	private SalesOrder createSalesOrder(int passengerTrips, String rate) {
		SalesOrder order = new SalesOrder();
		order.setCustomer(findContact(Contact.ContactType.CUSTOMER));
		order.setProduct(findProduct("Aerial Cable Passenger Transit"));
		order.setPassengerTrips(passengerTrips);
		order.setRatePerPassenger(money(rate));
		return salesOrderService.create(order);
	}

	private Invoice createInvoice(SalesOrder order) {
		Invoice request = new Invoice();
		request.setSalesOrder(order);
		return invoiceService.create(request);
	}

	private PurchaseOrder createPurchaseOrder(int quantity, String unitPrice) {
		return createPurchaseOrder(findProduct("Cable Tension Inspection"), quantity, unitPrice);
	}

	private PurchaseOrder createPurchaseOrder(Product product, int quantity, String unitPrice) {
		PurchaseOrder order = new PurchaseOrder();
		order.setVendor(findContact(Contact.ContactType.VENDOR));
		order.setProduct(product);
		order.setQuantity(quantity);
		order.setUnitPrice(money(unitPrice));
		return purchaseOrderService.create(order);
	}

	private VendorBill approveAndBill(PurchaseOrder order) {
		purchaseOrderService.approve(order.getId());
		VendorBill request = new VendorBill();
		request.setPurchaseOrder(order);
		return vendorBillService.create(request);
	}

	private JournalEntry findEntry(String description) {
		for (JournalEntry entry : entryRepository.findAll()) {
			if (description.equals(entry.getDescription())) return entry;
		}
		throw new AssertionError("Journal entry is missing: " + description);
	}

	private Telemetry telemetry(CableCar car, int passengers, String tension, String motorPower, String speed) {
		Telemetry telemetry = new Telemetry();
		telemetry.setCableCar(car);
		telemetry.setPassengerCount(passengers);
		telemetry.setCableTension(money(tension));
		telemetry.setMotorPowerUsage(money(motorPower));
		telemetry.setSpeed(money(speed));
		telemetry.setBatteryLevel(money("80"));
		telemetry.setTemperature(money("20"));
		return telemetry;
	}

	private long countAlerts(CableCar car, Alert.AlertType type) {
		return alertRepository.findAll().stream()
				.filter(alert -> alert.getCableCar().getId().equals(car.getId()) && alert.getType() == type).count();
	}

	private Contact findContact(Contact.ContactType type) {
		for (Contact contact : contactRepository.findAll()) {
			if (contact.getType() == type) return contact;
		}
		throw new AssertionError("Seed contact is missing: " + type);
	}

	private Product findProduct(String name) {
		for (Product product : productRepository.findAll()) {
			if (product.getName().equals(name)) return product;
		}
		throw new AssertionError("Seed product is missing: " + name);
	}

	private Payment customerPayment(Invoice invoice, String amount) {
		Payment payment = new Payment();
		payment.setInvoice(invoice);
		payment.setAmount(money(amount));
		return payment;
	}

	private Payment vendorPayment(VendorBill bill, String amount) {
		Payment payment = new Payment();
		payment.setVendorBill(bill);
		payment.setAmount(money(amount));
		return payment;
	}

	private BigDecimal money(String amount) { return new BigDecimal(amount); }
}
