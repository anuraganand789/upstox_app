package com.upstox.model.event;

/**
*  Represents a tickevent, 
*/
public interface TickEvent extends Comparable<TickEvent>{
    public static String OHLC_NOTIFY_EVENT = "ohlc_notify"; 
    /**
    *  this can be used by JSONObject parser generate json for an instance of tick event
    */
    String toJSONString();
}
