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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orfco.stoch.Stoch2.data.TickerCloseDataService;


@Service
public class Backtester {
	private static Logger logger = LoggerFactory.getLogger(Backtester.class);

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
		logger.error("1111111111111");
		// 1. Get symbols from properties file
		List<String> symbols = getSymbols();

		var tickerCloseData = dataService.initiateTickerCloseData(java.util.Arrays.asList("gs"));
	//	logger.info("TickerCloseData: {}:", tickerCloseData);
		// 5. Pull data from database

		// 6. Do analysis

	}

	private List<String> getSymbols() {
		logger.error("222222222222222222");
		URL fileUrl = ClassLoader.getSystemResource("backtest.properties");
		List<String> symbols = new ArrayList<String>();
		try {
			var backTestProps = new Properties();
			backTestProps.load(new FileInputStream(new File(fileUrl.getFile())));

			symbols = Arrays.asList(backTestProps.getProperty("symbols").split(","));

		} catch (FileNotFoundException ex) {
			logger.error("The properties file was not found.", ex);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			logger.error("There was a problem doing io stuff: ", ex);
		}
		return symbols;
	}



}
