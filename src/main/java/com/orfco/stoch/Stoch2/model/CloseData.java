package com.orfco.stoch.Stoch2.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class CloseData {
	private LocalDate closeDate;
	private int openPrice;
	private int highPrice;
	private int lowPrice;
	private int closePrice;
	private int volume;
	private int adjustedClose;

	public String toString() {
		StringBuffer buf = new StringBuffer("{");

		buf.append("\nCloseDate: " + this.getCloseDate().format(DateTimeFormatter.ISO_DATE) + ",\n");
		buf.append("ClosePrice: " + this.getClosePrice() + ",\n");
		buf.append("volume: " + this.getVolume() + "\n}");

		return buf.toString();
	}

}
