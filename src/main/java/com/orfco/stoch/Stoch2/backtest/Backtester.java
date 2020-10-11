package com.orfco.stoch.Stoch2.backtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orfco.stoch.Stoch2.data.TickerCloseDataService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class Backtester {
	
	@Autowired
	private TickerCloseDataService dataService;
	
	public Backtester() throws IOException {
	}

	public void run() throws Exception {
		log.error("11111ccccccc1111");

		var tickerCloseData = dataService.initiateTickerCloseData(java.util.Arrays.asList("gs"));
		log.info("TickerCloseData: {}:", tickerCloseData);
		// 5. Pull data from database

		// 6. Do analysis

	}




}
