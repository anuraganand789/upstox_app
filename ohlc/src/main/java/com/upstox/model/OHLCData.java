package com.upstox.model;

public class OHLCData{
    //sym
    private final String stockName;
    //P
    private final double priceOfTrade;
    //Q
    private final double quantityTraded;
    //TS2
    private final long timestampUTC;
    
    public OHLCData(final String stockName, 
                final double priceOfTrade, 
		final double quantityTraded, 
		final long timestampUTC){
        this.stockName = stockName;
	this.priceOfTrade = priceOfTrade;
	this.quantityTraded = quantityTraded;
	this.timestampUTC = timestampUTC;
    }

    public String getStockName() { return this.stockName; }

    public double getPriceOfTrade() { return this.priceOfTrade; }

    public double getQuantityTraded() { return this.quantityTraded; }

    public long getTimestampUTC() { return this.timestampUTC; }
}
