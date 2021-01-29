package com.orfco.stoch.Stoch2.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.orfco.stoch.Stoch2.data.TickerCloseApiAccess;
import com.orfco.stoch.Stoch2.data.TickerCloseMongoDAO;
import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.StartEndDatePair;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TickerCloseDataService {
	private ExecutorService executorService;

	private LocalDate epochStart;

	private TickerCloseMongoDAO dao;
	private TickerCloseApiAccess tickerCloseApi;
	
	private Map<String, TickerCloseData> closeDataCache = new HashMap<String, TickerCloseData>();

	@Autowired
	private TickerCloseDataService(@Value("${epoch.start.str}")String _epochStartStr,
			TickerCloseMongoDAO _mongoDao,
			TickerCloseApiAccess _closeApi) {
		epochStart = LocalDate.parse(_epochStartStr);
		dao = _mongoDao;
		tickerCloseApi = _closeApi;
	}

	public int getCount() {
		return dao.count();
	}

	public TickerCloseData getSingleTickerData(String symbol) throws Exception {
		List<String> symbols = List.of(symbol);
		return getSeveralTickersData(symbols).get(symbol);
	}

	public Map<String, TickerCloseData> getSeveralTickersData(List<String> symbols) throws Exception {
		log.info("initiateTickerCloseData called.");

		var closeDataBySymbol = new HashMap<String, TickerCloseData>();
		// loop thru all tickers
		symbols.stream().forEach(ticker -> {
			TickerCloseData tickerCloseData = null;
			// 1. Get data from cache if available
			if(closeDataCache.containsKey(ticker)
					&& LocalDate.now().compareTo(closeDataCache.get(ticker).getMostRecentClose()) <= 0)
			{
				tickerCloseData = closeDataCache.get(ticker);
			}

			// 2. Get data from database if it's not in the cache
			if(tickerCloseData == null)
				tickerCloseData = dao.getTickerCloseData(ticker);

			// 3. Get latest closeDate from tickerCloseData and define start-end dates for threaded work
			var latestDateForTicker = tickerCloseData.getMostRecentClose();
			var startEndDates = this.determineStartEndDates(latestDateForTicker);

			// 4. Get list of closes for start-end date pairs and add them to the tickerCloseData
			var closes = this.doThreadedDataWork(ticker, startEndDates)
					.stream()
					.sorted(Comparator.comparing(CloseData::getCloseDate))
					.collect(Collectors.toList());
			tickerCloseData.addCloseDataList(closes);
			var tickerCloses = tickerCloseData.getCloseData();
			var latestClose = tickerCloses.get(tickerCloses.size() - 1);
			tickerCloseData.setMostRecentClose(latestClose.getCloseDate());

			// 5. Insert TickerCloseData to database
			dao.insertTickerCloseToDatabase(tickerCloseData);

			// 6. Save that shit to cache and return object
			closeDataBySymbol.put(ticker, tickerCloseData);
			closeDataCache.put(ticker, tickerCloseData);

		});

		return closeDataBySymbol;

	}

	private List<CloseData> doThreadedDataWork(String _symbol, List<StartEndDatePair> startEndDatePairs) {
		log.info("doThreadedDataWork called.");

		if(startEndDatePairs == null)
			return new LinkedList<CloseData>();

		executorService = Executors.newFixedThreadPool(startEndDatePairs.size());

		// Mission here is to acquire CloseData within the date range *that we don't
		// already have in the DB
		// and add it to the DB
		var callables = new HashSet<TickerCloseCallable>();
		startEndDatePairs
			.stream()
			.forEach(t -> callables.add(new TickerCloseCallable(_symbol, t.getStartDate(), t.getEndDate())));
		var closeDatas = new LinkedList<CloseData>();

		try {
			List<Future<List<CloseData>>> closeDataFutures = executorService.invokeAll(callables);
			
			for(Future<List<CloseData>> closeDataFuture: closeDataFutures) {
				closeDatas.addAll(closeDataFuture.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executorService.shutdown();

		return closeDatas;
	}

	private List<StartEndDatePair> determineStartEndDates(LocalDate latestCloseInDatabase) {
		log.info("determineStartEndDates called.");
		var start = latestCloseInDatabase == LocalDate.MIN ? epochStart : latestCloseInDatabase;
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

			return closeData;
		}
	}

}
