package com.upstox.consumer;

import com.upstox.model.OHLCData;
import com.upstox.model.OHLCEvent;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.upstox.queue.PacketsBlockingQueue;

public class FiniteStateMachine implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(FiniteStateMachine.class.getName());

    private final Set<String> setOfStocks = new HashSet<>(100);

    private final Map<String, Integer>    mapOfStockToBarNumber   = new LinkedHashMap<>();
    private final Map<String, Long>       mapOfStockToFirstTrade  = new LinkedHashMap<>();
    private final Map<String, Boolean>    mapOfStockToExpiration  = new LinkedHashMap<>();
    private final Map<String, StockState> mapOfStockToState       = new LinkedHashMap<>();
    private final Map<String, OHLCEvent>  mapOfStockToEvent       = new LinkedHashMap<>();

    private void consume() throws InterruptedException{
	OHLCData ohlcData;
        while(true){
	   ohlcData = PacketsBlockingQueue.read(); 
	   final String stockName    = ohlcData.getStockName();
	   final long   timestampUTC = ohlcData.getTimestampUTC();
	   expireTheStocks(timestampUTC);
	   updateBarNum(timestampUTC);
	   updateState(stockName, timestampUTC);
	}
    }

    private void updateState(final String stockName, final long timestampUTC){
        if(setOfStocks.add(stockName)) { 
	    mapOfStockToFirstTrade.put(stockName, timestampUTC);
	    mapOfStockToBarNum(stockName, 1);
	}
        mapOfStockState.put(stockName, ohlcData);
    }

    private int calculateTheTick(final String stockName, final long timestampUTC){
        //return timestamp diff in seconds
    }

    private void updateBarNum(final long timestampUTC){
        //starts from 1, and increases by 1 for each passed tick
    }

    private void exprireTheStocks(final long currentTime){
       //if a stock has closed then remove it from map of stock to state map, we only need the state for 15 seconds interval,
       //after that the value is no needed.
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
