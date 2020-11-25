package com.orfco.stoch.Stoch2.data.access;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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

	public LocalDate getLatestForSymbol(String _symbol) {
		return this.getTickerCloseData(_symbol).getMostRecentClose();
	}

	public boolean insertTickerCloseToDatabase(TickerCloseData _tickerCloseData) {
		log.info("insertTickerClosetoDatabase batch insert called. ");
		mongoTemplate.save(_tickerCloseData);

		return true;
	}

	public TickerCloseData getTickerCloseData(String _symbol) {
		log.info("getTickerCloseData called.");

		Query query = new Query();
		query.addCriteria(
				Criteria
				.where("ticker").is(_symbol))
				.with(Sort.by(Sort.Direction.ASC,"mostRecentClose")
		);

		TickerCloseData closeData = null;
		var savedCloses = mongoTemplate.find(query, TickerCloseData.class);
		if(!savedCloses.isEmpty())
			closeData = savedCloses.get(0);
		else
			closeData = TickerCloseData.build(_symbol, null);

		return closeData;
	}



}
