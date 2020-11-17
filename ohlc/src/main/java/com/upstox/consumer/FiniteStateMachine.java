package com.upstox.consumer;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.atomic.AtomicInteger;

import java.time.Duration;

import com.upstox.queue.OHLCBarDataQueue;
import com.upstox.queue.PacketsBlockingQueue;

import com.upstox.model.OHLCData;

import com.upstox.model.event.TickEvent;
import com.upstox.model.event.EmptyEvent;
import com.upstox.model.event.OHLCEvent;

import java.time.temporal.ChronoUnit;

import static com.upstox.util.TimeUtils.calculateTheTick;

/**
*   It consumes data from OHLCData queue. 
*   Calculates the tick and pushes it to the Tick-Queue
*/
public class FiniteStateMachine implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(FiniteStateMachine.class.getName());
   
    /**
    *  A set of all the stacks, read from json file
    */
    private final Set<String> setOfStocks = new HashSet<>(100);
    /**
    *  Total number of active stocks
    */
    private final Set<String> setOfStocksWithActiveInterval = new HashSet<>(100);

    /**
    *  contains map of Stock  to the open price of the stock
    */
    private final Map<String, Double>     mapOfStockToOpen          = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the high price of the stock
    */
    private final Map<String, Double>     mapOfStockToHigh          = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the Low price of the stock
    */
    private final Map<String, Double>     mapOfStockToLow           = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the Close price of the stock
    */
    private final Map<String, Double>     mapOfStockToClose         = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the Volume price of the stock
    */
    private final Map<String, Double>     mapOfStockToVolume        = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the LastPrice price of the stock
    */
    private final Map<String, Double>     mapOfStockToLastPrice     = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the  current starttime of the interval
    */
    private final Map<String, Long>       mapOfStockToIntervalStart = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the current endTime of the interval
    */
    private final Map<String, Long>       mapOfStockToIntervalEnd   = new LinkedHashMap<>();
    /**
    *  contains map of Stock  to the current bar number 
    */
    private final Map<String, Integer>    mapOfStockToBarNumber     = new LinkedHashMap<>();
    
    /**
    *  Maintains a map of stock to it's tick timer
    */
    private final Map<String, AtomicInteger> mapOfStockToTick = new LinkedHashMap<>();

    /**
    *  Reads OHLCData from queue. 
    *  And, process those data
    */
    private void consume() throws InterruptedException{
        final Timer  tickUpdater = new Timer("Update Stock Tick");
	tickUpdater.schedule(new TimerTask() {
	                      @Override
			      public void run(){
			          for(final String stockName : setOfStocks){
				      if(mapOfStockToTick.containsKey(stockName)) { 
				          int remainingSecond = mapOfStockToTick.get(stockName).decrementAndGet();
					  if(remainingSecond < 1) {
					      expireTheStock(stockName);
					  }
				      }
				  }
			      }
	                  }, 0, 1000);

	OHLCData ohlcData;
        while(true){
	   ohlcData = PacketsBlockingQueue.read(); 

	   final String stockName    = ohlcData.getStockName();
	   final long   timestampUTC = ohlcData.getTimestampUTC();

	   expireTheStocks(timestampUTC);
	   setOfStocks.add(stockName);
	   updateOHLC(ohlcData);
	   updateBarNumber(timestampUTC);
	   pushOHLCEventToQueue(stockName);
	}

    }

    /**
    *  Loops through the stock, updates the bar number
    *  @param currentTimestamp The current time received from the OHLCData packet
    */
    private void updateBarNumber(final long currentTimestamp){
	for(final String stockName : setOfStocks){
	    if(mapOfStockToBarNumber.containsKey(stockName) && !setOfStocksWithActiveInterval.contains(stockName)){
                final long lastTimestamp        = mapOfStockToIntervalStart.get(stockName);
	        final Duration lastTimeDuration = Duration.ofNanos(lastTimestamp);

	        long diffInSeconds = calculateTheTick(currentTimestamp, lastTimestamp, ChronoUnit.SECONDS);
                
		if(diffInSeconds > 15){
	            long divisor;
	            while((divisor = diffInSeconds / 15) > 0){
	                diffInSeconds -= 15;
                        lastTimeDuration.plusSeconds(15);

	                incrementBarNum(stockName, 1);

			long endOfInterval = lastTimeDuration.toNanos();

			mapOfStockToIntervalEnd.put(stockName, endOfInterval);
	                mapOfStockToIntervalStart.put(stockName, endOfInterval + 1);
	            }
		}
	    } 	
	}

    }

    /**
    *  Creates an empty event for the given stock name.
    *  @param stockName the stock name whose empty event is generated
    */
    private TickEvent createEmptyTickEvent(final String stockName){
	return new EmptyEvent(TickEvent.OHLC_NOTIFY_EVENT, stockName, mapOfStockToBarNumber.get(stockName));
    }

    /** 
    *  Push a tick event to the blocking queue, to be consumed by WebSocketService
    *  @param stockName stockName of the target stock, whose tick event needs to be pushed
    */
    private void pushOHLCEventToQueue(final String stockName){
	final TickEvent tickEvent = createEventObject(stockName);
	System.out.println(tickEvent);
    }

    /**
    *   Increases the bar number of the given stock name by the amount of increseBy
    *   @param stockName    The stock name of the target stock, whose bar number needs to be modified
    *   @param increaseBy   the quantitiy by which bar number needs to be increased
    */
    private void incrementBarNum(final String stockName, final int increaseBy){
        this.mapOfStockToBarNumber.computeIfPresent(stockName, (key, value) -> value + increaseBy);
    }
    
    /**
    *   Update O H L C parameters of a stock
    *   @param ohlcData a ohlcData from queue
    */
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

	setOfStocksWithActiveInterval.add(stockName);

	mapOfStockToBarNumber.putIfAbsent(stockName, 1);
        mapOfStockToIntervalStart.putIfAbsent(stockName, timestampUTC);
	mapOfStockToTick.put(stockName, new AtomicInteger(15));
    }

    /**
    *  Goes through the set of stocks and checks there time between start and end intervals.
    *  And, updates their start and end intervals
    *  @param currentTime the time which will be used to calcualte the expiration time
    */
    private void expireTheStock(final String stockName){
        closeTheStock(stockName);
        final TickEvent tickEvent = setOfStocksWithActiveInterval.contains(stockName) ? createEventObject(stockName) : createEmptyTickEvent(stockName);

        System.out.println(tickEvent);

        pushToOHLCQueue(tickEvent);

	mapOfStockToOpen.remove(stockName);
	mapOfStockToHigh.remove(stockName);
	mapOfStockToLow.remove(stockName);
	mapOfStockToClose.remove(stockName);
	mapOfStockToVolume.remove(stockName);

        setOfStocksWithActiveInterval.remove(stockName);

	incrementBarNum(stockName, 1);
	mapOfStockToTick.put(stockName, new AtomicInteger(15));
    }
    /**
    *  Goes through the set of stocks and checks there time between start and end intervals.
    *  And, updates their start and end intervals
    *  @param currentTime the time which will be used to calcualte the expiration time
    */
    private void expireTheStocks(final long currentTime){
       Iterator<String> iterator = setOfStocks.iterator();
       String stockName;
       while(iterator.hasNext()){
           stockName = iterator.next(); 
           long noOfSecondsPassed = calculateTheTick(currentTime, mapOfStockToIntervalStart.get(stockName), ChronoUnit.SECONDS);
	   if(noOfSecondsPassed > 15) { 
	       closeTheStock(stockName);
	       final TickEvent tickEvent = setOfStocksWithActiveInterval.contains(stockName) ? createEventObject(stockName) : createEmptyTickEvent(stockName);

	       System.out.println(tickEvent);
	       pushToOHLCQueue(tickEvent);

	       mapOfStockToOpen.remove(stockName);
	       mapOfStockToHigh.remove(stockName);
	       mapOfStockToLow.remove(stockName);
	       mapOfStockToClose.remove(stockName);
	       mapOfStockToVolume.remove(stockName);

               setOfStocksWithActiveInterval.remove(stockName);

	       incrementBarNum(stockName, 1);
	   } 
       }
    }
    
    /**
    *  A tick event generated by FiniteStateMachine.
    *  @param ohlcEvent a tick event that needs to sent to the tick-queue
    */
    private void pushToOHLCQueue(final TickEvent ohlcEvent){
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
    
    /**
    *  Closes the stock, by updating the close time
    *  @param stockName the name of the stock, whose interval needs to be closed
    */
    private void closeTheStock(final String stockName){
        mapOfStockToClose.put(stockName, mapOfStockToLastPrice.get(stockName));

	final long startInterval = mapOfStockToIntervalStart.get(stockName);
	final long endInterval   = Duration.ofNanos(startInterval).plusSeconds(15).toNanos();

	mapOfStockToIntervalEnd.put(stockName, endInterval);
	mapOfStockToIntervalStart.put(stockName, endInterval + 1);
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
