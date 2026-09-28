package com.example.SKYTRANS;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import com.example.SKYTRANS.model.Account;
import com.example.SKYTRANS.model.AnalyticAccount;
import com.example.SKYTRANS.model.Budget;
import com.example.SKYTRANS.model.CableCar;
import com.example.SKYTRANS.model.Contact;
import com.example.SKYTRANS.model.Invoice;
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
import com.example.SKYTRANS.repository.CableCarRepository;
import com.example.SKYTRANS.repository.ContactRepository;
import com.example.SKYTRANS.repository.ProductRepository;
import com.example.SKYTRANS.repository.TerminalRepository;
import com.example.SKYTRANS.repository.TransitRouteRepository;
import com.example.SKYTRANS.repository.TripRepository;
import com.example.SKYTRANS.service.BudgetService;
import com.example.SKYTRANS.service.InvoiceService;
import com.example.SKYTRANS.service.PaymentService;
import com.example.SKYTRANS.service.PurchaseOrderService;
import com.example.SKYTRANS.service.SalesOrderService;
import com.example.SKYTRANS.service.TelemetryService;
import com.example.SKYTRANS.service.TripService;
import com.example.SKYTRANS.service.VendorBillService;

// Extra demo data, loaded once by SampleDataLoader after the basic records exist.
// Master data is saved with repositories. Trips, orders, invoices, bills and payments go
// through the same services as the REST API, so they follow every business rule and
// automatically create their journal entries.
@Component
public class SampleBusinessFlowLoader {
	private final ContactRepository contactRepository;
	private final ProductRepository productRepository;
	private final AccountRepository accountRepository;
	private final CableCarRepository cableCarRepository;
	private final TerminalRepository terminalRepository;
	private final TransitRouteRepository routeRepository;
	private final TripRepository tripRepository;
	private final TripService tripService;
	private final TelemetryService telemetryService;
	private final PurchaseOrderService purchaseOrderService;
	private final VendorBillService vendorBillService;
	private final SalesOrderService salesOrderService;
	private final InvoiceService invoiceService;
	private final PaymentService paymentService;
	private final BudgetService budgetService;

	public SampleBusinessFlowLoader(ContactRepository contactRepository, ProductRepository productRepository,
			AccountRepository accountRepository, CableCarRepository cableCarRepository,
			TerminalRepository terminalRepository, TransitRouteRepository routeRepository,
			TripRepository tripRepository, TripService tripService, TelemetryService telemetryService,
			PurchaseOrderService purchaseOrderService, VendorBillService vendorBillService,
			SalesOrderService salesOrderService, InvoiceService invoiceService, PaymentService paymentService,
			BudgetService budgetService) {
		this.contactRepository = contactRepository;
		this.productRepository = productRepository;
		this.accountRepository = accountRepository;
		this.cableCarRepository = cableCarRepository;
		this.terminalRepository = terminalRepository;
		this.routeRepository = routeRepository;
		this.tripRepository = tripRepository;
		this.tripService = tripService;
		this.telemetryService = telemetryService;
		this.purchaseOrderService = purchaseOrderService;
		this.vendorBillService = vendorBillService;
		this.salesOrderService = salesOrderService;
		this.invoiceService = invoiceService;
		this.paymentService = paymentService;
		this.budgetService = budgetService;
	}

	public void load() {
		addContacts();
		addProducts();
		addFleetAndNetwork();
		addTripsAndTelemetry();
		addPurchases();
		addSales();
		addBudgets();
	}

	// ---------- Master data ----------

	private void addContacts() {
		contactRepository.save(new Contact("City Tourism Board", Contact.ContactType.CUSTOMER,
				"passes@tourism.example", "555-0140", "Visitor Centre, Old Town"));
		contactRepository.save(new Contact("Metro Rail Corporation", Contact.ContactType.CUSTOMER,
				"billing@metrorail.example", "555-0150", "Metro House, City Centre"));
		contactRepository.save(new Contact("Northgrid Power Utility", Contact.ContactType.VENDOR,
				"accounts@northgrid.example", "555-0160", "Grid Control Centre"));
		contactRepository.save(new Contact("Alpine Rope Works", Contact.ContactType.VENDOR,
				"sales@alpinerope.example", "555-0170", "Industrial Park, Unit 7"));
	}

	private void addProducts() {
		saveProduct("Tourist Day Pass", "Unlimited rides for one day", "SERVICE", "35.00", "4000");
		saveProduct("Grid Electricity Supply", "Monthly traction power supply", "SERVICE", "12000.00", "5000");
		saveProduct("Haul Rope Replacement", "New steel haul rope for the cable line", "PRODUCT", "150000.00", "1100");
		saveProduct("Gondola Cabin Unit", "Ten-seat gondola cabin", "PRODUCT", "450000.00", "1150");
	}

	private void addFleetAndNetwork() {
		cableCarRepository.save(new CableCar("CR-104", 45, money("55")));
		cableCarRepository.save(new CableCar("TR-201", 50, money("40")));

		terminalRepository.save(new Terminal("CEN", "Central Plaza", "City Centre"));
		Terminal depot = new Terminal("DPT", "Mountain Depot", "Upper Ridge");
		depot.setStatus(Terminal.Status.MAINTENANCE);
		terminalRepository.save(depot);

		saveRoute("MNT-03", "NTH", "SUM", "10.5", 25);
		saveRoute("TRM-02", "EST", "CEN", "2.4", 7);
	}

	// ---------- Transit operations ----------

	private void addTripsAndTelemetry() {
		CableCar cr104 = car("CR-104");
		CableCar tr201 = car("TR-201");

		completeTrip(scheduleTrip(cr104, "MNT-03", 30));
		completeTrip(scheduleTrip(tr201, "TRM-02", 42));
		tripService.start(scheduleTrip(tr201, "TRM-02", 35).getId()); // still running

		Trip cancelled = scheduleTrip(car("CR-102"), "MNT-02", 20);
		cancelled.setStatus(Trip.Status.CANCELLED);
		tripRepository.save(cancelled);

		// Normal readings inside all safety limits (no alerts)
		telemetryService.receive(telemetry(cr104, 30, "72", "280", "48", "91", "18"));
		telemetryService.receive(telemetry(cr104, 28, "95", "310", "50", "88", "19"));
		telemetryService.receive(telemetry(tr201, 35, "68", "240", "32", "76", "21"));
		telemetryService.receive(telemetry(car("CR-101"), 18, "65", "205", "36", "84", "29"));
	}

	// ---------- Purchasing ----------

	private void addPurchases() {
		Contact northgrid = contact("Northgrid Power Utility");
		Contact alpine = contact("Alpine Rope Works");

		// Electricity: ordered, approved, billed and fully paid
		VendorBill powerBill = approveAndBill(createPurchaseOrder(northgrid, "Grid Electricity Supply", 1));
		payVendor(powerBill, "12000.00");

		// New haul rope (an asset): billed and partly paid
		VendorBill ropeBill = approveAndBill(createPurchaseOrder(alpine, "Haul Rope Replacement", 1));
		payVendor(ropeBill, "50000.00");

		// Waiting for approval
		createPurchaseOrder(alpine, "Gondola Cabin Unit", 2);

		// Approved, ready to be billed
		purchaseOrderService.approve(createPurchaseOrder(contact("Cable Engineering Specialist"), "Motor Drive Overhaul", 1).getId());

		// Cancelled order
		purchaseOrderService.cancel(createPurchaseOrder(northgrid, "Grid Electricity Supply", 1).getId());
	}

	// ---------- Sales ----------

	private void addSales() {
		// Day passes: invoiced and fully paid
		Invoice tourismInvoice = createInvoice(createSalesOrder("City Tourism Board", "Tourist Day Pass", 800));
		receivePayment(tourismInvoice, "28000.00");

		// Passenger transit: invoiced and partly paid
		Invoice metroInvoice = createInvoice(createSalesOrder("Metro Rail Corporation", "Aerial Cable Passenger Transit", 2500));
		receivePayment(metroInvoice, "20000.00");

		// Waiting to be invoiced
		createSalesOrder("Municipal Transit Authority", "Aerial Cable Passenger Transit", 1500);
	}

	// ---------- Budgets ----------

	private void addBudgets() {
		saveBudget("Transit Corridor - East Tram Line", "East District tram service", "120000.00", "20000.00", "15000.00");
		saveBudget("Transit Corridor - Summit Express", "Express gondola to Mountain Summit", "90000.00", "18000.00", "25000.00");
	}

	// ---------- Small helpers ----------

	private void saveProduct(String name, String description, String type, String price, String accountCode) {
		Product product = new Product(name, description, type, money(price));
		product.setAccount(account(accountCode));
		productRepository.save(product);
	}

	private void saveRoute(String code, String sourceCode, String destinationCode, String distance, int minutes) {
		TransitRoute route = new TransitRoute();
		route.setRouteCode(code);
		route.setSourceTerminal(terminal(sourceCode));
		route.setDestinationTerminal(terminal(destinationCode));
		route.setDistance(money(distance));
		route.setEstimatedTime(minutes);
		routeRepository.save(route);
	}

	private Trip scheduleTrip(CableCar cableCar, String routeCode, int passengers) {
		Trip trip = new Trip();
		trip.setCableCar(cableCar);
		trip.setRoute(route(routeCode));
		trip.setPassengerCount(passengers);
		return tripService.create(trip);
	}

	private void completeTrip(Trip trip) {
		tripService.start(trip.getId());
		tripService.complete(trip.getId());
	}

	private Telemetry telemetry(CableCar cableCar, int passengers, String tension, String motorPower,
			String speed, String battery, String temperature) {
		Telemetry telemetry = new Telemetry();
		telemetry.setCableCar(cableCar);
		telemetry.setPassengerCount(passengers);
		telemetry.setCableTension(money(tension));
		telemetry.setMotorPowerUsage(money(motorPower));
		telemetry.setSpeed(money(speed));
		telemetry.setBatteryLevel(money(battery));
		telemetry.setTemperature(money(temperature));
		return telemetry;
	}

	private PurchaseOrder createPurchaseOrder(Contact vendor, String productName, int quantity) {
		PurchaseOrder order = new PurchaseOrder();
		order.setVendor(vendor);
		order.setProduct(product(productName));
		order.setQuantity(quantity);
		return purchaseOrderService.create(order);
	}

	private VendorBill approveAndBill(PurchaseOrder order) {
		purchaseOrderService.approve(order.getId());
		VendorBill request = new VendorBill();
		request.setPurchaseOrder(order);
		return vendorBillService.create(request);
	}

	private void payVendor(VendorBill bill, String amount) {
		Payment payment = new Payment();
		payment.setVendorBill(bill);
		payment.setAmount(money(amount));
		paymentService.payVendor(payment);
	}

	private SalesOrder createSalesOrder(String customerName, String productName, int passengerTrips) {
		SalesOrder order = new SalesOrder();
		order.setCustomer(contact(customerName));
		order.setProduct(product(productName));
		order.setPassengerTrips(passengerTrips);
		return salesOrderService.create(order);
	}

	private Invoice createInvoice(SalesOrder order) {
		Invoice request = new Invoice();
		request.setSalesOrder(order);
		return invoiceService.create(request);
	}

	private void receivePayment(Invoice invoice, String amount) {
		Payment payment = new Payment();
		payment.setInvoice(invoice);
		payment.setAmount(money(amount));
		paymentService.receiveCustomerPayment(payment);
	}

	private void saveBudget(String corridorName, String description, String revenue, String electricity,
			String maintenance) {
		AnalyticAccount corridor = new AnalyticAccount();
		corridor.setName(corridorName);
		corridor.setDescription(description);
		corridor = budgetService.createAnalyticAccount(corridor);
		Budget budget = new Budget();
		budget.setAnalyticAccount(corridor);
		budget.setBudgetYear(LocalDate.now().getYear());
		budget.setPlannedRevenue(money(revenue));
		budget.setElectricityBudget(money(electricity));
		budget.setMaintenanceBudget(money(maintenance));
		budgetService.create(budget);
	}

	private Contact contact(String name) { return contactRepository.findByName(name).orElseThrow(); }
	private Product product(String name) { return productRepository.findByName(name).orElseThrow(); }
	private Account account(String code) { return accountRepository.findByAccountCode(code).orElseThrow(); }
	private CableCar car(String vehicleNumber) { return cableCarRepository.findByVehicleNumber(vehicleNumber).orElseThrow(); }
	private Terminal terminal(String code) { return terminalRepository.findByTerminalCode(code).orElseThrow(); }
	private TransitRoute route(String code) { return routeRepository.findByRouteCode(code).orElseThrow(); }
	private BigDecimal money(String amount) { return new BigDecimal(amount); }
}
