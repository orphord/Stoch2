package com.orfco.stoch.Stoch2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class TickerCloseData {
	
	@Id
	private ObjectId id;
	private String ticker;
	private LocalDate mostRecentClose = LocalDate.MIN;
	private List<CloseData> closeData = new ArrayList<CloseData>();

	public static TickerCloseData build(String _ticker, List<CloseData> _closeData) {
		log.error("TickerCloseData static builder called.");
		var tickerCloses = new TickerCloseData();
		tickerCloses.setTicker(_ticker == null ? "" : _ticker);
		tickerCloses.setCloseData(
				Optional.ofNullable(_closeData).map(Collection::stream).orElseGet(Stream::empty)
				.sorted(Comparator.comparing(CloseData::getCloseDate))
				.collect(Collectors.toList()) );

		int numCloses = tickerCloses.getCloseData().size();
		if (numCloses > 0) 
				tickerCloses.setMostRecentClose(tickerCloses.getCloseData().get(numCloses - 1).getCloseDate());

		return tickerCloses;
	}

	public void addCloseData(CloseData _close) {
		closeData.add(_close);
	}
	
	public void addCloseDataList(List<CloseData> _closes) {
		closeData.addAll(_closes);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("\nTickerClose {")
				.append("\n\tsymbol: " + ticker)
				.append("\n\tmostRectentClose: " + mostRecentClose.toString())
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
