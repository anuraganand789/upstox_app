package com.upstox.model.event;

/**
*  Represents and empty event, the scenarios where not data has come for a given stock
*/
public class EmptyEvent implements TickEvent{
    private final String event;
    private final String symbol;
    private final int    barnum;

    public EmptyEvent(final String event, final String symbol, final int barnum){
        this.event  = event;
	this.symbol = symbol;
	this.barnum = barnum;
    }

    /**
    *  The class to json converter library will use this function to generate json
    */
    @Override
    public String toJSONString(){
	return new StringBuilder(100)
	                             .append("{")
	                                 .append('"').append("event")  .append('"').append(':').append('"').append(this.event).append('"').append(',')
	                                 .append('"').append("symbol") .append('"').append(':').append('"').append(this.symbol).append('"').append(',')
	                                 .append('"').append("bar_num").append('"').append(':').append(this.barnum) 
	                             .append("}")
				                 .toString();
    }
    
    /**
    * All TickEvent have equal priotity
    */
    @Override
    public int compareTo(TickEvent tickEvent){
        return 0;
    }

    @Override
    public String toString(){
        return toJSONString();
    }
}
