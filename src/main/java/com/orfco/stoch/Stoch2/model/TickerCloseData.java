package com.orfco.stoch.Stoch2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class TickerCloseData implements Iterable<CloseData> {
	private String ticker;
	private LocalDate mostRecentClose = LocalDate.MIN;
	
	List<CloseData> closeData = new ArrayList<CloseData>();

	public Iterator<CloseData> iterator() {
		return closeData.iterator();
	}

	public void addCloseData(CloseData _close) {
		closeData.add(_close);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("\nTickerClose {")
				.append("\n\tsymbol: " + ticker)
				.append("\n\tcloses: " +
						closeData
						.stream()
						.sorted(Comparator.comparing(CloseData::getCloseDate))
						.collect(Collectors.toList())
						.toString())
				.append("\n")
				.append("\n}");
		return buf.toString();
	}
}
