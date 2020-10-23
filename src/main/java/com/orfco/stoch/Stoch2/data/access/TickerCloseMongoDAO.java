package com.orfco.stoch.Stoch2.data.access;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TickerCloseMongoDAO implements TickerCloseDAO {

	@Autowired
	private MongoTemplate mongoTemplate;

	public int count() {
		return (int)mongoTemplate.count(new Query(), "CloseData");
	}

	public LocalDate getLatestForSymbol(String symbol) {
		return LocalDate.MIN;
	}

	static int testcount = 0;

	public int[] insertTickerCloseToDatabase(List<CloseData> _closeData, String _ticker) {
		log.info("insertTickerClosetoDatabase batch insert called.");

		// ticker, closedate, closepriceincents, openpriceincents,lowpricecents,
		// highpricecents, adjclosepricecents, volume, moddatetime
		return new int[3];

	}

	public TickerCloseData getTickerCloseData(String _symbol) {
		log.info("getTickerCloseData called.");

		TickerCloseData closeData = new TickerCloseData();
		closeData.setTicker(_symbol);
		return closeData;
	}

}
