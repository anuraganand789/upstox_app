package com.upstox.model.event;

/**
*  contains data for one incoming trade data
*/
public class OHLCEvent implements TickEvent{
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private String symbol;
    private int    barnum;

    public OHLCEvent(final double open,   final double high, 
                     final double low,    final double close,
                     final double volume, final String symbol, 
		     final int    barnum){
        this.open   = open;
	this.high   = high;
	this.low    = low;
	this.close  = close;
	this.volume = volume;
	this.symbol = symbol;
        this.barnum = barnum;
    }

    public double getOpen(){ return this.open; }
    public double getClose(){ return this.close; }
    public double getHigh(){ return this.high; }
    public double getLow(){ return this.low; }
    public double getVolume(){ return this.volume; }

    public String getSymbol(){ return this.symbol; }

    public int    getBarnum(){ return this.barnum; }

    /**
    *   Returns json data representation for this class. This function will be used by the Json marshaller.
    */
    @Override
    public String toJSONString(){
       return new StringBuilder(150)
                                    .append('{')
                                        .append('"')  .append("o")        .append('"').append(':') .append(this.open)     .append(',')
                                        .append('"')  .append("h")        .append('"').append(':') .append(this.high)     .append(',')
                                        .append('"')  .append("l")        .append('"').append(':') .append(this.low)      .append(',')
                                        .append('"')  .append("c")        .append('"').append(':') .append(this.close)    .append(',')

                                        .append('"')  .append("volume")   .append('"').append(':') .append(this.volume)   .append(',')
                                        .append('"')  .append("bar_num")  .append('"').append(':') .append(this.barnum)   .append(',')

                                        .append('"')  .append("event")    .append('"').append(':') .append('"')           .append(OHLC_NOTIFY_EVENT)  .append('"') .append(',')
                                        .append('"')  .append("symbol")   .append('"').append(':') .append('"')           .append(this.symbol)        .append('"')
                                    .append('}')
	                                        .toString();
    }

    /**
    *  All Event have equal priotity
    */
    @Override
    public int compareTo(final TickEvent tickEvent){
       return 0; 
    }

    @Override
    public String toString(){
        return toJSONString();
    }

}
