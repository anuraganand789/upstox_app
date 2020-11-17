package com.upstox.model;

import java.util.Objects;

/**
*   A class which holds OHLC details, 
*/
public class OHLCData implements Comparable<OHLCData>{
    private final String stockName;
    private final double priceOfTrade;
    private final double quantityTraded;
    private final long   timestampUTC;
    
    public OHLCData(final String stockName, 
                    final double priceOfTrade, 
		    final double quantityTraded, 
		    final long   timestampUTC){
        this.stockName        = stockName;
	this.priceOfTrade     = priceOfTrade;
	this.quantityTraded   = quantityTraded;
	this.timestampUTC     = timestampUTC;
    }

    public String getStockName()      { return this.stockName; }

    public double getPriceOfTrade()   { return this.priceOfTrade; }

    public double getQuantityTraded() { return this.quantityTraded; }

    public long   getTimestampUTC()   { return this.timestampUTC; }

    public int hashCode() { return stockName.hashCode(); }

    public boolean equals(final Object obj){
	return (obj instanceof OHLCData) && Objects.equals(((OHLCData) obj).getStockName(),this.stockName);
    }

    /**
    *   Used in priotity Queue to maintain the correct order while taking data from the queue
    */
    @Override
    public int compareTo(final OHLCData ohlcData){
        return Long.compare(this.getTimestampUTC(), ohlcData.getTimestampUTC()); 
    }

    @Override
    public String toString(){
        return new StringBuilder(100)
	               .append("StockName ").append(stockName) 
	               .append(',').append("Price OF Trade ").append(priceOfTrade) 
	               .append(',').append("Quantitytraded ").append(quantityTraded) 
	               .append(',').append("TimestampUTC ")  .append(timestampUTC)
		       .toString();
    }
}
