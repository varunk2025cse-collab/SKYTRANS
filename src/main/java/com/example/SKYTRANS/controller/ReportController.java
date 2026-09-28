package com.example.SKYTRANS.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.SKYTRANS.service.PassengerReportService;
import com.example.SKYTRANS.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
	private final ReportService reportService;
	private final PassengerReportService passengerReportService;

	public ReportController(ReportService reportService, PassengerReportService passengerReportService) {
		this.reportService = reportService;
		this.passengerReportService = passengerReportService;
	}

	@GetMapping("/balance-sheet")
	public Map<String, Object> balanceSheet() { return reportService.getBalanceSheet(); }

	@GetMapping("/profit-loss")
	public Map<String, Object> profitAndLoss() { return reportService.getProfitAndLoss(); }

	@GetMapping("/budget")
	public List<Map<String, Object>> budget() { return reportService.getBudgetReport(); }

	@GetMapping("/passengers/daily")
	public Map<String, Object> dailyPassengers(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		LocalDate reportDate = date == null ? LocalDate.now() : date;
		return Map.of("date", reportDate, "passengerTrips", passengerReportService.getDailyPassengerCount(reportDate));
	}
}