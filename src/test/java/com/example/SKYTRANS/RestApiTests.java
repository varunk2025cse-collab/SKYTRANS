package com.example.SKYTRANS;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

// Sends real JSON requests (the same shape the web page sends) through the REST controllers.
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class RestApiTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void tripAndTelemetryAcceptCableCarReferenceById() throws Exception {
		postJson("/api/trips", "{\"cableCar\":{\"id\":1},\"route\":{\"id\":1},\"passengerCount\":10}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("SCHEDULED"));
		postJson("/api/cablecar/telemetry", "{\"cableCar\":{\"id\":1},\"passengerCount\":5,\"cableTension\":60,"
				+ "\"motorPowerUsage\":100,\"speed\":20,\"batteryLevel\":80,\"temperature\":20}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	void salesOrderInvoiceAndPaymentFlowWorksOverHttp() throws Exception {
		ResultActions order = postJson("/api/sales-orders",
				"{\"customer\":{\"id\":1},\"product\":{\"id\":1},\"passengerTrips\":5,\"ratePerPassenger\":20}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalAmount").value(100));
		ResultActions invoice = postJson("/api/invoices", "{\"salesOrder\":{\"id\":" + idOf(order) + "}}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(100))
				.andExpect(jsonPath("$.status").value("UNPAID"));
		postJson("/api/payments/customer", "{\"invoice\":{\"id\":" + idOf(invoice) + "},\"amount\":101}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(containsString("cannot exceed the outstanding balance")));
		postJson("/api/payments/customer", "{\"invoice\":{\"id\":" + idOf(invoice) + "},\"amount\":100}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.invoice.status").value("PAID"));
	}

	@Test
	void purchaseOrderVendorBillFlowWorksOverHttp() throws Exception {
		ResultActions order = postJson("/api/purchase-orders",
				"{\"vendor\":{\"id\":2},\"product\":{\"id\":2},\"quantity\":2,\"unitPrice\":150}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalAmount").value(300));
		long orderId = idOf(order);
		postJson("/api/vendor-bills", "{\"purchaseOrder\":{\"id\":" + orderId + "}}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Approve the purchase order before creating its bill"));
		mockMvc.perform(post("/api/purchase-orders/" + orderId + "/approve")).andExpect(status().isOk());
		postJson("/api/vendor-bills", "{\"purchaseOrder\":{\"id\":" + orderId + "}}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(300));
	}

	@Test
	void invalidInputReturnsClearMessages() throws Exception {
		postJson("/api/cablecar/telemetry", "{\"cableCar\":{\"id\":1},\"passengerCount\":5,\"cableTension\":60,"
				+ "\"motorPowerUsage\":100,\"speed\":20,\"batteryLevel\":150,\"temperature\":20}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("batteryLevel must be less than or equal to 100.0"));
		postJson("/api/trips", "{\"cableCar\":{\"id\":2},\"route\":{\"id\":1},\"passengerCount\":55}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Passenger count must be between 0 and 50 for this cable car"));
		postJson("/api/contacts", "{\"name\":\"X\",\"type\":\"BOGUS\"}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("The request data is invalid or has a wrong value type"));
		postJson("/api/routes", "{\"routeCode\":\"LOOP\",\"sourceTerminal\":{\"id\":1},\"destinationTerminal\":{\"id\":1},"
				+ "\"distance\":1,\"estimatedTime\":2}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Source and destination terminals must be different"));
	}

	private ResultActions postJson(String url, String json) throws Exception {
		return mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
	}

	// Reads the "id" of the record returned by the server
	private long idOf(ResultActions result) throws Exception {
		String body = result.andReturn().getResponse().getContentAsString();
		return ((Number) JsonPath.read(body, "$.id")).longValue();
	}
}
