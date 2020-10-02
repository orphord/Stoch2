package com.orfco.stoch.Stoch2.data.mapping;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.orfco.stoch.Stoch2.model.CloseData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonCloseDataAdapter extends TypeAdapter<List<CloseData>> {

	public static final String PRICES = "prices";
	public static final String CLOSEDATE = "date";
	public static final String OPENPRICE = "open";
	public static final String CLOSEPRICE = "close";
	public static final String HIGHPRICE = "high";
	public static final String LOWPRICE = "low";
	public static final String ADJUSTEDCLOSE = "adjClose";
	public static final String VOLUME = "volume";

	@Override
	public void write(JsonWriter out, List<CloseData> value) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CloseData> read(JsonReader reader) throws IOException {
		List<CloseData> closeData = new ArrayList<CloseData>();

		JsonElement root = JsonParser.parseReader(reader);
		JsonArray priceArray = root.getAsJsonArray();

		for(var close : priceArray) {
			var temp = buildCloseDataFromJsonElement(close);
			closeData.add(temp);
		}

		return closeData;
	}
	
	private CloseData buildCloseDataFromJsonElement(JsonElement priceElement) {
		CloseData datum = new CloseData();
		JsonObject priceObj = priceElement.getAsJsonObject();

		DateTimeFormatter incomingDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		datum.setCloseDate(LocalDate.parse(priceObj.get(CLOSEDATE).getAsString(), incomingDateFormat));
		datum.setClosePrice((int)(priceObj.get(CLOSEPRICE).getAsFloat() * 100));
		datum.setOpenPrice((int)(priceObj.get(OPENPRICE).getAsFloat() * 100));
		datum.setHighPrice((int)(priceObj.get(HIGHPRICE).getAsFloat() * 100));
		datum.setLowPrice((int)(priceObj.get(LOWPRICE).getAsFloat() * 100));
		datum.setAdjustedClose((int)(priceObj.get(ADJUSTEDCLOSE).getAsFloat() * 100));
		datum.setVolume(priceObj.get(VOLUME).getAsInt());

		return datum;
	}

}
