package com.orfco.stoch.Stoch2.backtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orfco.stoch.Stoch2.data.TickerCloseDataService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class Backtester {
	
	@Autowired
	private TickerCloseDataService dataService;
	
	private List<String> tickers = new ArrayList<String>();
	
	public Backtester() throws IOException {
	}

	public Backtester(String _symbol) throws IOException {
		this();
		tickers.add(_symbol);

	}

	public void addSymbol(String _symbol) {
		tickers.add(_symbol);
	}

	public void run() throws Exception {
		log.error("1111111111111");
		// 1. Get symbols from properties file
		List<String> symbols = getSymbols();

		var tickerCloseData = dataService.initiateTickerCloseData(java.util.Arrays.asList("gs"));
	//	logger.info("TickerCloseData: {}:", tickerCloseData);
		// 5. Pull data from database

		// 6. Do analysis

	}

	private List<String> getSymbols() {
		log.error("222222222222222222");
		URL fileUrl = ClassLoader.getSystemResource("backtest.properties");
		List<String> symbols = new ArrayList<String>();
		try {
			var backTestProps = new Properties();
			backTestProps.load(new FileInputStream(new File(fileUrl.getFile())));
			symbols = Arrays.asList(backTestProps.getProperty("symbols").split(","));

		} catch (FileNotFoundException ex) {
			log.error("The properties file was not found.", ex);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			log.error("There was a problem doing io stuff: ", ex);
		}
		return symbols;
	}



}
