package com.orfco.stoch.Stoch2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orfco.stoch.Stoch2.data.TickerCloseDataService;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

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
	public TickerCloseData backtestTicker(@PathVariable String ticker) throws Exception {
		log.info("Backtest ticker: {}", ticker);
		TickerCloseData outCloseData = null;
		outCloseData = dataService.getSingleTickerData(ticker);

		log.info("=====================");
		return outCloseData;
	}


}
