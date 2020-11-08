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

    @Override
    public String toJSONString(){
	final StringBuilder sb = new StringBuilder(100);
	sb.append("{");
	    sb.append("\"event\":");  sb.append("\""); sb.append(this.event);   sb.append("\""); sb.append(",");
	    sb.append("\"symbol\":"); sb.append("\""); sb.append(this.symbol);  sb.append("\""); sb.append(",");
	    sb.append("\"bar_num\":");  sb.append(this.barnum); 
	sb.append("}");
        return sb.toString();
    }
    
    @Override
    public int compareTo(TickEvent tickEvent){
        return 0;
    }

    @Override
    public String toString(){
        return toJSONString();
    }
}
