package com.orfco.stoch.Stoch2.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class StartEndDatePair {
	private LocalDate startDate;
	private LocalDate endDate;

	private StartEndDatePair(LocalDate _start, LocalDate _end) {
		startDate = _start;
		endDate = _end;
	}
	
	public static StartEndDatePair build(LocalDate _startDate, LocalDate _endDate) {
		return new StartEndDatePair(_startDate, _endDate);
	}

	public String toString() {
		return new StringBuffer("Start: " + startDate.toString() + "; End: " + endDate.toString()).toString();
	}
}
