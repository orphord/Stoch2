package com.orfco.stoch.Stoch2.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orfco.stoch.Stoch2.data.access.TickerCloseApiAccess;
import com.orfco.stoch.Stoch2.data.access.TickerCloseDAO;
import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.StartEndDatePair;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TickerCloseDataService {
	private ExecutorService executorService;

	private String epochStartStr = "2019-04-20";
	private LocalDate epochStart;

	@Autowired
	private TickerCloseDAO dao;

	@Autowired
	private TickerCloseApiAccess tickerCloseApi;

	private TickerCloseDataService() {
		epochStart = LocalDate.parse(epochStartStr);
	}

	public Map<String, TickerCloseData> initiateTickerCloseData(List<String> symbols) throws Exception {
		log.info("initiateTickerCloseData called.");
		// 2. check if what earliest date range we need data for form database
		var closeDataBySymbol = new HashMap<String, TickerCloseData>();
		symbols.stream().forEach(ticker -> {
			var latestDateForTicker = dao.getLatestForSymbol(ticker);
			var startEndDates = this.determineStartEndDates(latestDateForTicker);
			this.doThreadedDataWork(ticker, startEndDates);
		});
		symbols.stream().forEach(ticker -> closeDataBySymbol.put(ticker, dao.getTickerCloseData(ticker)));

		return closeDataBySymbol;

	}

	private void doThreadedDataWork(String _symbol, List<StartEndDatePair> startEndDatePairs) {
		log.info("doThreadedDataWork called.");
		executorService = Executors.newFixedThreadPool(startEndDatePairs.size());

		// Mission here is to acquire CloseData within the date range *that we don't
		// already have in the DB*
		// and add it to the DB
		var callables = new HashSet<TickerCloseCallable>();
		startEndDatePairs.stream()
			.forEach(t -> callables.add(new TickerCloseCallable(_symbol, t.getStartDate(), t.getEndDate())));

		try {
			executorService.invokeAll(callables);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executorService.shutdown();
	}

	private List<StartEndDatePair> determineStartEndDates(LocalDate latestCloseInDatabase) {
		log.info("determineStartEndDates called.");
		var start = latestCloseInDatabase == null ? epochStart : latestCloseInDatabase;
		var today = LocalDate.now();

		var datePairList = new ArrayList<StartEndDatePair>();

		// Return null if today is the latest close in the database -- we don't want to
		// reach out to the web service
		if (today.equals(latestCloseInDatabase))
			return null;

		// We know that today is different that the latest close so we should add the
		// most recent year to the
		if (today.getYear() == start.getYear()) {
			datePairList.add(StartEndDatePair.build(start.plusDays(1), today));
			return datePairList;
		}

		// Set the year to be tested each loop to *this* year and loop back to the
		// earliest
		// year for which we want data -- either the last date for which we already have
		// close
		// data in the database *or* the beginning of the "epoch" (the longest ago date
		// for which
		// we are interested in close data)
		IntStream.rangeClosed(start.getYear(), today.getYear())
			.forEach(year -> {
				LocalDate yearBeginDate = LocalDate.of(year, 1, 1);
				LocalDate yearEndDate = LocalDate.of(year, 12, 31);
				if (year == today.getYear())
					yearEndDate = today;
				if (year == start.getYear())
					yearBeginDate = start.plusDays(1);

				datePairList.add(StartEndDatePair.build(yearBeginDate, yearEndDate));
			});

		return datePairList;
	}

	class TickerCloseCallable implements Callable<List<CloseData>> {
		private String symbol;
		private LocalDate startDate;
		private LocalDate endDate;

		private TickerCloseCallable(String _symbol, LocalDate _startDate, LocalDate _endDate) {
			symbol = _symbol;
			startDate = _startDate;
			endDate = _endDate;
		}

		@Override
		public List<CloseData> call() throws Exception {
			log.info("thread to get and insert to database.");
			var closeData = tickerCloseApi.getCloseData(symbol, startDate, endDate);
			dao.insertTickerCloseToDatabase(closeData, symbol);
			return closeData;
		}
	}

}
