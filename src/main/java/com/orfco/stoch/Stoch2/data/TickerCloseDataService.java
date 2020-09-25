package com.orfco.stoch.Stoch2.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orfco.stoch.Stoch2.data.access.TickerCloseApiAccess;
import com.orfco.stoch.Stoch2.data.access.TickerCloseDAO;
import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

@Service
public class TickerCloseDataService {
	private static Logger logger = LoggerFactory.getLogger(TickerCloseDataService.class);

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

	public TickerCloseData initiateTickerCloseData(List<String> symbols) throws Exception {
		// 2. check if what earliest date range we need data for form database
		var symbol = "gs";
		var latestCloseInDatabase = dao.getLatestForSymbol(symbol);

		// We're talking about dates here to get
		// 1. create start/end date pairs based on how far back we need to to in history
		// for symbols
		var startEndDatePairs = determineStartEndDates(latestCloseInDatabase);

		// 2. create number of threads based on number of pairs
		doThreadedDataWork(symbol, startEndDatePairs);

		// 3. get TickerCloseData from database
		var tickerCloseData = dao.getTickerCloseData(symbol);

		return tickerCloseData;
	}

	private void doThreadedDataWork(String _symbol, List<StartEndDatePair> startEndDatePairs)
			throws Exception {
		executorService = Executors.newFixedThreadPool(startEndDatePairs.size());
		
		// Mission here is to acquire CloseData within the date range *that we don't already have in the DB*
		// and add it to teh DB
		var callables = new HashSet<TickerCloseCallable>();
		for(var startEndPair : startEndDatePairs) {
			callables.add(new TickerCloseCallable(_symbol, startEndPair.startDate, startEndPair.endDate));
		}

		executorService.invokeAll(callables);

		executorService.shutdown();
	}

	private List<StartEndDatePair> determineStartEndDates(LocalDate latestCloseInDatabase) {
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
			datePairList.add(new StartEndDatePair(start.plusDays(1), today));
			return datePairList;
		}

		// Set the year to be tested each loop to *this* year and loop back to the earliest
		// year for which we want data -- either the last date for which we already have close
		// data in the database *or* the beginning of the "epoch" (the longest ago date for which
		// we are interested in close data)
		for (int testYear = today.getYear(); start.getYear() <= testYear; testYear--) {
			LocalDate yearBeginDate = LocalDate.of(testYear, 1, 1);
			LocalDate yearEndDate = LocalDate.of(testYear, 12, 31);
			if (testYear == today.getYear())
				yearEndDate = today;
			if (testYear == start.getYear())
				yearBeginDate = start.plusDays(1);

			datePairList.add(new StartEndDatePair(yearBeginDate, yearEndDate));
		}

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
			logger.info("TickerCloseCallable call() called.");
			var closeData = tickerCloseApi.getCloseData(symbol, startDate, endDate);
			dao.insertTickerCloseToDatabase(closeData, symbol);
			return closeData;
		}
	}

	class StartEndDatePair {
		private LocalDate startDate;
		private LocalDate endDate;

		private StartEndDatePair(LocalDate _start, LocalDate _end) {
			setStartDate(_start);
			setEndDate(_end);
		}

		public String toString() {
			return new StringBuffer("\nStartEndDatePair: {").append("\n\tStartDate: ").append(startDate)
			    .append("\n\tEndDate: ").append(endDate).append("\n}").toString();
		}

		/**
		 * @return the startDate
		 */
		public LocalDate getStartDate() {
			return startDate;
		}

		/**
		 * @param _startDate the startDate to set
		 */
		public void setStartDate(LocalDate _startDate) {
			this.startDate = _startDate;
		}

		/**
		 * @return the endDate
		 */
		public LocalDate getEndDate() {
			return endDate;
		}

		/**
		 * @param endDate the endDate to set
		 */
		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}
	}
}
