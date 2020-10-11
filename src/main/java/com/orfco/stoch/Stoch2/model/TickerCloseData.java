package com.orfco.stoch.Stoch2.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class TickerCloseData implements Iterable<CloseData> {
	private String ticker;
	List<CloseData> closeData = new ArrayList<CloseData>();

	public Iterator<CloseData> iterator() {
		return closeData.iterator();
	}

	public void addCloseData(CloseData _close) {
		closeData.add(_close);
	}
	
	public List<CloseData> getCloseData(){
		return closeData;
	}
	
	public void setCloseData(List<CloseData> _closeData) {
		closeData = _closeData;
	}

	/**
	 * @return the ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/**
	 * @param _ticker the ticker to set
	 */
	public void setTicker(String _ticker) {
		ticker = _ticker;
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
