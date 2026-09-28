package com.example.SKYTRANS;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SkytransApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void allNavigationPagesRender() throws Exception {
		String[] pages = { "dashboard", "cable-cars", "terminals", "routes", "trips", "telemetry", "contacts",
				"products", "accounts", "accounting", "purchase-orders", "vendor-bills", "sales-orders", "invoices",
				"payments", "budgets", "reports" };
		for (String page : pages) {
			mockMvc.perform(get("/").param("page", page)).andExpect(status().isOk());
		}
	}

}
