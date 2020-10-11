package com.orfco.stoch.Stoch2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orfco.stoch.Stoch2.data.TickerCloseDataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("tickertest")
@Slf4j
public class StochController {

	@Autowired
	private TickerCloseDataService dataService;

	@GetMapping("/")
	public String baseUrl() {
		log.info("BaseUrl accessed.");
		return "values";
	}
	
	@GetMapping("/{ticker}")
	public void backtestTicker(@PathVariable String ticker) {
		log.info("Backtest ticker: {}", ticker);
		try {
			dataService.initiateTickerCloseData(java.util.Arrays.asList(ticker));
		} catch (Exception e) {
			log.error("Exception thrown when initiatingTickerCloseData thing.");
			e.printStackTrace();
		}
		log.info("=====================");
	}


}
