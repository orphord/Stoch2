package com.orfco.stoch.Stoch2.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CloseData {
	private LocalDate closeDate;
	private int openPrice;
	private int highPrice;
	private int lowPrice;
	private int closePrice;
	private int volume;
	private int adjustedClose;

	/**
	 * @return the closeDate
	 */
	public LocalDate getCloseDate() {
		return closeDate;
	}

	/**
	 * @param _closeDate the closeDate to set
	 */
	public void setCloseDate(LocalDate _closeDate) {
		closeDate = _closeDate;
	}

	/**
	 * @return the openPrice
	 */
	public int getOpenPrice() {
		return openPrice;
	}

	/**
	 * @param _openPrice the openPrice to set
	 */
	public void setOpenPrice(int _openPrice) {
		openPrice = _openPrice;
	}

	/**
	 * @return the highPrice
	 */
	public int getHighPrice() {
		return highPrice;
	}

	/**
	 * @param _highPrice the highPrice to set
	 */
	public void setHighPrice(int _highPrice) {
		highPrice = _highPrice;
	}

	/**
	 * @return the lowPrice
	 */
	public int getLowPrice() {
		return lowPrice;
	}

	/**
	 * @param _lowPrice the lowPrice to set
	 */
	public void setLowPrice(int _lowPrice) {
		lowPrice = _lowPrice;
	}

	/**
	 * @return the closePrice
	 */
	public int getClosePrice() {
		return closePrice;
	}

	/**
	 * @param _closePrice the closePrice to set
	 */
	public void setClosePrice(int _closePrice) {
		closePrice = _closePrice;
	}

	/**
	 * @return the volume
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * @param _volume the volume to set
	 */
	public void setVolume(int _volume) {
		volume = _volume;
	}

	/**
	 * @return the adjustedClose
	 */
	public int getAdjustedClose() {
		return adjustedClose;
	}

	/**
	 * @param _adjustedClose the adjustedClose to set
	 */
	public void setAdjustedClose(int _adjustedClose) {
		adjustedClose = _adjustedClose;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("\nCloseData for Date: " + this.getCloseDate() + "\n");

		buf.append("CloseDate: " + this.getCloseDate().format(DateTimeFormatter.ISO_DATE) + "\n");
		buf.append("ClosePrice: " + this.getClosePrice() + "\n");
		buf.append("volume: " + this.getVolume() + "\n");

		return buf.toString();
	}

}
