package com.orfco.stoch.Stoch2.data.mapping;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.TickerCloseData;



public class TickerCloseDataExtractor implements ResultSetExtractor<TickerCloseData> {
	private static Logger logger = LoggerFactory.getLogger(TickerCloseDataExtractor.class);

	@Override
	public TickerCloseData extractData(ResultSet rs) throws SQLException, DataAccessException {
		logger.info("TickerCloseDataExtractor.extractData method called.");
		TickerCloseData tickerCloseData = new TickerCloseData();

		while(rs.next()) {
			var close = new CloseData();
			close.setCloseDate(rs.getDate("closedate").toLocalDate());
			close.setClosePrice(rs.getInt("closepricecents"));
			close.setOpenPrice(rs.getInt("openpricecents"));
			close.setLowPrice(rs.getInt("lowpricecents"));
			close.setHighPrice(rs.getInt("highpricecents"));
			close.setAdjustedClose(rs.getInt("adjclosepricecents"));
			close.setVolume(rs.getInt("volume"));
			tickerCloseData.addCloseData(close);
			
		}

		return tickerCloseData;
	}

}
