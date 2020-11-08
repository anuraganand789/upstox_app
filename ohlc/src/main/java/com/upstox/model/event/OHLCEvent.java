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

    public OHLCEvent(final double open, final double high, final double low, final double close,
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

    public int getBarnum(){ return this.barnum; }

    /**
    *   Returns json data representation for this class
    */
    @Override
    public String toJSONString(){
        StringBuilder sb = new StringBuilder(150);

        sb.append("{");

            sb.append("\"o\":");        sb.append(this.open);     sb.append(",");
            sb.append("\"h\":");        sb.append(this.high);     sb.append(",");
            sb.append("\"l\":");        sb.append(this.low);      sb.append(",");
            sb.append("\"c\":");        sb.append(this.close);    sb.append(",");

            sb.append("\"volume\":");   sb.append(this.volume);   sb.append(",");
            sb.append("\"bar_num\":");  sb.append(this.barnum);   sb.append(",");

            sb.append("\"event\":");    sb.append("\"");          sb.append(OHLC_NOTIFY_EVENT);  sb.append("\""); sb.append(",");
            sb.append("\"symbol\":");   sb.append("\"");          sb.append(this.symbol);        sb.append("\"");

        sb.append("}");

        return sb.toString();
    }

    @Override
    public int compareTo(final TickEvent tickEvent){
       return 0; 
    }

    @Override
    public String toString(){
        return toJSONString();
    }

}
