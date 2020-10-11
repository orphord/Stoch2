package com.orfco.stoch.Stoch2.data.access;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.orfco.stoch.Stoch2.data.mapping.GsonCloseDataAdapter;
import com.orfco.stoch.Stoch2.model.CloseData;
import com.orfco.stoch.Stoch2.model.TickerCloseData;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class TickerCloseApiAccess {
	
	public List<CloseData> getCloseData(String _symbol, LocalDate startDate, LocalDate endDate)
			throws IOException {
		// 1. Create correct URL...
		var startDateStr = startDate == null ? "2020-01-01" : startDate.toString();
		var endDateStr = "&endDate=" + (endDate == null ? LocalDate.now().toString() : endDate.toString());
		var url = "https://api.tiingo.com/tiingo/daily/" + _symbol
				+ "/prices?startDate=" + startDateStr + endDateStr + "&token=b77f6f9bae3b3e731697c72010d38bf5f9df8a95";

		List<CloseData> closes = new ArrayList<CloseData>();
		HttpResponse<String> response = null;
		try {
			log.info("Getting data from URL: {}", url);
			response = Unirest.get(url).header("'Content-Type", "application/json").asString();

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ArrayList.class, new GsonCloseDataAdapter());
			closes = gsonBuilder.create().fromJson(response.getBody(), ArrayList.class);

		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return closes;
	}
}
