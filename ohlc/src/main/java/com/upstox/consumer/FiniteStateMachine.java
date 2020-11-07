package com.upstox.consumer;

import com.upstox.model.OHLCData;
import com.upstox.model.OHLCEvent;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.upstox.queue.PacketsBlockingQueue;

public class FiniteStateMachine implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(FiniteStateMachine.class.getName());

    private final Map<String, Integer>    mapOfStockToBarNumber   = new LinkedHashMap<>();
    private final Map<String, Long>       mapOfStockToFirstTrade  = new LinkedHashMap<>();
    private final Map<String, Boolean>    mapOfStockToExpiration  = new LinkedHashMap<>();
    private final Map<String, StockState> mapOfStockToState       = new LinkedHashMap<>();
    private final Map<String, OHLCEvent>  mapOfStockToEvent       = new LinkedHashMap<>();

    private void consume() throws InterruptedException{
	OHLCData ohlcData;
        while(true){
	   ohlcData = PacketsBlockingQueue.read(); 
	}
    }

    private void updateState(final OHLCData ohlcData){
	final String stockName    = ohlcData.getStockName();
	final long   timestampUTC = ohlcData.getTimestampUTC();
        mapOfStockToFirstTrade.putIfAbsent(stockName, timestampUTC);
	expireTheStocks(timestampUTC);
        mapOfStockState.put(stockName, ohlcData);
    }

    private void exprireTheStocks(final long currentTime){
        
    }

    public OHLCData generateIntervalData(){
	computeDataPer15Seconds();
	constructBarChartData();
        return null;
    }

    private void computeDataPer15Seconds(){
    }

    private void constructBarChartData(){
    }

    @Override
    public void run(){
        try{ 
	    consume();
	}catch(InterruptedException ex){
	    LOGGER.info("Interrupted Exception " + ex.getMessage());
	}
    }
}
