package com.upstox.test.model.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.upstox.model.event.EmptyEvent;
import com.upstox.model.event.OHLCEvent;

import static com.upstox.model.event.TickEvent.OHLC_NOTIFY_EVENT;

public class OHLCEventTest{
       private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private String symbol;
    private int    barnum;
 
    @Test
    @DisplayName("Verifying json string for not empty Tick")
    public void ohlcTickJson(){
	final String expected = "{\"o\":0.0,\"h\":0.0,\"l\":0.0,\"c\":0.0,\"volume\":12.1,\"bar_num\":2,\"event\":\"ohlc_notify\",\"symbol\":\"XXBTZUSD\"}";
	final OHLCEvent ohlcEvent = new OHLCEvent(0.0, 0.0, 0.0, 0.0, 12.1, "XXBTZUSD", 2);
        assertTrue(expected.equals(ohlcEvent.toJSONString()));
    }

    @Test
    @DisplayName("Verifying json string for empty Tick")
    public void emptyTickJson(){
	final String expected = "{\"event\":\"ohlc_notify\",\"symbol\":\"XXBTZUSD\",\"bar_num\":1}";
	final EmptyEvent emptyEvent = new EmptyEvent(OHLC_NOTIFY_EVENT, "XXBTZUSD", 1);
        assertTrue(expected.equals(emptyEvent.toJSONString()));
    }
}
