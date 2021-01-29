package com.orfco.stoch.Stoch2.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.orfco.stoch.Stoch2.data.mapping.TickerCloseDataExtractor;
import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TickerCloseMysqlDAO implements TickerCloseDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int count() {
		return jdbcTemplate.queryForObject("select count(*) from CloseData", Integer.class);
	}

	public LocalDate getLatestForSymbol(String symbol) {
		return jdbcTemplate.queryForObject("select max(closedate) from CloseData where ticker = ?", new Object[] { symbol },
		    LocalDate.class);
	}

	static int testcount = 0;

	public boolean insertTickerCloseToDatabase(TickerCloseData _tickerCloseData) {
		log.info("insertTickerClosetoDatabase batch insert called.");
		var closeData = _tickerCloseData.getCloseData();
		var ticker = _tickerCloseData.getTicker();
		
		// ticker, closedate, closepriceincents, openpriceincents,lowpricecents,
		// highpricecents, adjclosepricecents, volume, moddatetime
		return (jdbcTemplate.batchUpdate("INSERT INTO CloseData VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, DEFAULT)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						log.info("TestCount: {}", ++testcount);
						ps.setString(1, ticker);
						ps.setObject(2, closeData.get(i).getCloseDate());
						ps.setInt(3, closeData.get(i).getClosePrice());
						ps.setInt(4, closeData.get(i).getOpenPrice());
						ps.setInt(5, closeData.get(i).getLowPrice());
						ps.setInt(6, closeData.get(i).getHighPrice());
						ps.setInt(7, closeData.get(i).getAdjustedClose());
						ps.setInt(8, closeData.get(i).getVolume());
					}

					@Override
					public int getBatchSize() {
						return closeData.size();
					}
				}).length == closeData.size());

	}

	public TickerCloseData getTickerCloseData(String _symbol) {
		log.info("getTickerCloseData called.");
		var preparedStatementCreator = new PreparedStatementCreator() {
			String query = "SELECT * from CloseData WHERE ticker = ? order by closedate";

			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement prepStatement = connection.prepareStatement(query);
				prepStatement.setString(1, _symbol);
				return prepStatement;
			}
		};

		TickerCloseData closeData = jdbcTemplate.query(preparedStatementCreator, new TickerCloseDataExtractor());
		closeData.setTicker(_symbol);
		return closeData;
	}

}
