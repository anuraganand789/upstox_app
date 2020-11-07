package com.upstox.consumer;

import com.upstox.model.OHLCData;
import com.upstox.model.OHLCEvent;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;

import java.time.Duration;

import com.upstox.queue.OHLCBarDataQueue;
import com.upstox.queue.PacketsBlockingQueue;


//TODO: log the event packets
//TODO: fix the bar numbers
public class FiniteStateMachine implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(FiniteStateMachine.class.getName());
   
    private final PacketsBlockingQueue blockingQueue;

    private final Set<String> setOfStocks = new HashSet<>(100);
    private final Set<String> setOfStocksWithActiveInterval = new HashSet<>(100);

    private final Map<String, Double>     mapOfStockToOpen        = new LinkedHashMap<>();
    private final Map<String, Double>     mapOfStockToHigh        = new LinkedHashMap<>();
    private final Map<String, Double>     mapOfStockToLow         = new LinkedHashMap<>();
    private final Map<String, Double>     mapOfStockToClose       = new LinkedHashMap<>();
    private final Map<String, Double>     mapOfStockToVolume      = new LinkedHashMap<>();
    private final Map<String, Double>     mapOfStockToLastPrice   = new LinkedHashMap<>();
    private final Map<String, Long>       mapOfStockToTimeStamp   = new LinkedHashMap<>();
    private final Map<String, Integer>    mapOfStockToBarNumber   = new LinkedHashMap<>();


    private final Map<String, Long>       mapOfStockToFirstTrade  = new LinkedHashMap<>();
    private final Map<String, Boolean>    mapOfStockToExpiration  = new LinkedHashMap<>();
    private final Map<String, OHLCEvent>  mapOfStockToEvent       = new LinkedHashMap<>();

    public FiniteStateMachine(final PacketsBlockingQueue blockingQueue){
        this.blockingQueue = blockingQueue;
    }

    private void consume() throws InterruptedException{
	OHLCData ohlcData;
        while(true){
	   ohlcData = PacketsBlockingQueue.read(); 

	   final String stockName    = ohlcData.getStockName();
	   final long   timestampUTC = ohlcData.getTimestampUTC();

	   expireTheStocks(stockName, timestampUTC);
	   updateOHLC(ohlcData);
	   emitEmptyEvents(timestampUTC);
	}
    }

    private void pushEmptyEventToQueue(final String stockName){
    }

    private void emitEmptyEvents(final long currentTimeStamp){
        for(final String stockName : setOfStocks){
	    if(setOfStocksWithActiveInterval.contains(stockName)){ continue; }
            int seconds = calculateTheTick(currentTimeStamp, mapOfStockToTimeStamp.get(stockName)); 
	    if(seconds > 14){
	        pushEmptyEventToQueue(stockName);
		updateLastTimeStamp(stockName, currentTimeStamp);
	    }
	}
    }

    private void updateLastTimeStamp(final String stockName, final long currentTimestamp){
        final long lastTimestamp      = mapOfStockToTimeStamp.get(stockName);
	final Duration lastTimeDuration = Duration.ofNanos(lastTimestamp);
	int diffInSeconds = calculateTheTick(currentTimestamp, lastTimestamp);
        
	int divisor;
	while((divisor = diffInSeconds / 15) > 0){
	    diffInSeconds -= 15;
            lastTimeDuration.plusSeconds(15);
	    incrementBarNum(stockName, 1);
	    mapOfStockToTimeStamp.put(stockName, lastTimeDuration.toNanos());
	}
    }

    private void incrementBarNum(final String stockName, final int increaseBy){
        this.mapOfStockToBarNumber.computeIfPresent(stockName, (key, value) -> value + increaseBy);
    }
    
    private void updateOHLC(final OHLCData ohlcData){
        final String stockName    = ohlcData.getStockName();
	final long   timestampUTC = ohlcData.getTimestampUTC();

	final double currentStockPrice  = ohlcData.getPriceOfTrade();
	final double currentTradeVolume = ohlcData.getQuantityTraded();

	mapOfStockToLastPrice.put(stockName, currentStockPrice);

	if(setOfStocksWithActiveInterval.contains(stockName)){
	    final double oldHighPrice = mapOfStockToHigh.get(stockName);
	    if(currentStockPrice > oldHighPrice) { mapOfStockToHigh.put(stockName, currentStockPrice); } 

	    final double oldLowPrice = mapOfStockToLow.get(stockName);
	    if(currentStockPrice < oldLowPrice) { mapOfStockToLow.put(stockName, currentStockPrice); }

            mapOfStockToVolume.put(stockName, Double.sum(currentTradeVolume, mapOfStockToVolume.get(stockName)));
	    return;
	}

        mapOfStockToOpen     .put(stockName, currentStockPrice); 
        mapOfStockToHigh     .put(stockName, currentStockPrice);
        mapOfStockToLow      .put(stockName, currentStockPrice);
        mapOfStockToClose    .put(stockName, 0.0);
        mapOfStockToVolume   .put(stockName, currentTradeVolume);
	mapOfStockToTimeStamp.put(stockName, timestampUTC);
    }

    private void updateState(final String stockName, final long timestampUTC){
        if(setOfStocks.add(stockName)) { 
	    mapOfStockToFirstTrade.put(stockName, timestampUTC);
	    incrementBarNum(stockName, 1);
	}
        //mapOfStockState.put(stockName, ohlcData);
    }

    private int calculateTheTick(final long currentTimestamp, final long lastTimestamp){
        final Duration lastDuration = Duration.ofNanos(lastTimestamp); 
	final Duration currentDuration = Duration.ofNanos(currentTimestamp);
        return currentDuration.minus(lastDuration).toSecondsPart();
    }

    private void expireTheStocks(final String stockName, final long currentTime){
       Iterator<String> iterator = setOfStocksWithActiveInterval.iterator();
       String currentStockName;
       while(iterator.hasNext()){
           currentStockName = iterator.next(); 
           int noOfSecondsPassed = calculateTheTick(currentTime, mapOfStockToTimeStamp.get(stockName));
	   if(noOfSecondsPassed > 14) { 
	       iterator.remove(); 
	       //close the stock and put it in the Event Queue of Socket 
	       closeTheStock(stockName);
	       final OHLCEvent ohlcEvent = createEventObject(stockName);
	       pushToOHLCQueue(ohlcEvent);
	   } 
       }
       //if a stock has closed then remove it from map of stock to state map, we only need the state for 15 seconds interval,
       //after that the value is no needed.
    }
    
    private void pushToOHLCQueue(final OHLCEvent ohlcEvent){
        try{
            OHLCBarDataQueue.write(ohlcEvent);
        }catch(InterruptedException ex){
	    LOGGER.info("Interrupted Exception " + ex.getMessage());
        }
    }

    private OHLCEvent createEventObject(final String stockName){
        final OHLCEvent event = new OHLCEvent(
					       mapOfStockToOpen.get(stockName),
					       mapOfStockToHigh.get(stockName),
					       mapOfStockToLow.get(stockName),
					       mapOfStockToClose.get(stockName),
					       mapOfStockToVolume.get(stockName),
					       stockName,
					       mapOfStockToBarNumber.get(stockName)
	                                     );
        return event;
    }
    
    private void closeTheStock(final String stockName){
        mapOfStockToClose.put(stockName, mapOfStockToLastPrice.get(stockName));
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
