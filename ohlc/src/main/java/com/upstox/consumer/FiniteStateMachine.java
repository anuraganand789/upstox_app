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

    private final Map<String, Double>    mapOfStockToOpen        = new LinkedHashMap<>();
    private final Map<String, Double>    mapOfStockToHigh        = new LinkedHashMap<>();
    private final Map<String, Double>    mapOfStockToLow         = new LinkedHashMap<>();
    private final Map<String, Double>    mapOfStockToClose       = new LinkedHashMap<>();
    private final Map<String, Double>    mapOfStockToVolume      = new LinkedHashMap<>();
    private final Map<String, Integer>   mapOfStockToBarNumber   = new LinkedHashMap<>();

    private final Map<String, Long>       mapOfStockToFirstTrade  = new LinkedHashMap<>();
    private final Map<String, Boolean>    mapOfStockToExpiration  = new LinkedHashMap<>();
    private final Set<String>             setOfStockWithActiveInterval = new HashSet<>();
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
	  // updateState(stockName, timestampUTC);
	   updateOHLC(ohlcData);
	}
    }
    
    private void updatOHLC(final OHLCData ohlcData){
	final String stockName    = ohlcData.getStockName();
	final long   timestampUTC = ohlcData.getTimestampUTC();

	final double currentStockPrice  = ohlcData.getPriceOfTrade();
	final double currentTradeVolume = ohlcData.getQuantityTraded();

	if(setOfStockWithActiveInterval.contains(stockName)){
	    final double oldHighPrice = mapOfStockToHigh.get(stockName);
	    if(currentStockPrice > oldHighPrice) { mapOfStockToHigh.put(stockName, currentStockPrice); } 

	    final double oldLowPrice = mapOfStockToLow.get(stockName);
	    if(currentStockPrice < oldLowPrice) { mapOfStockToLow.put(stockName, currentStockPrice); }

            mapOfStockToVolume.put(stockName, Double.sum(currentTradeVolume, mapOfStockToVolume.get(stockName));
	    return;
	}

        mapOfStockToOpen  .put(stockName, currentStockPrice); 
        mapOfStockToHigh  .put(stockName, currentStockPrice);
        mapOfStockToLow   .put(stockName, currentStockPrice);
        mapOfStockToClose .put(stockName, 0);
        mapOfStockToVolume.put(stockName, currentTradeVolume);
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
