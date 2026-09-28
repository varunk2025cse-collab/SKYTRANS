package com.example.SKYTRANS.controller;

import java.util.List;

// One row of a table on the web page: the cell values plus an optional action button
// (for example "Start trip" which calls POST /api/trips/5/start).
public class TableRow {
	private final List<String> cells;
	private String actionLabel;
	private String actionUrl;

	public TableRow(List<String> cells) {
		this.cells = cells;
	}

	public void setAction(String actionLabel, String actionUrl) {
		this.actionLabel = actionLabel;
		this.actionUrl = actionUrl;
	}

	public List<String> getCells() { return cells; }
	public String getActionLabel() { return actionLabel; }
	public String getActionUrl() { return actionUrl; }
}
