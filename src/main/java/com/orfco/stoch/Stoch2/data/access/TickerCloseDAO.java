package com.orfco.stoch.Stoch2.data.access;

import java.time.LocalDate;
import java.util.List;

import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

public interface TickerCloseDAO {
	public int count();
	public LocalDate getLatestForSymbol(String _symbol);
	public boolean insertTickerCloseToDatabase(TickerCloseData _tickerCloseData);
	public TickerCloseData getTickerCloseData(String _symbol);
}
