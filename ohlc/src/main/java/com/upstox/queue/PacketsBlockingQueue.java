package com.upstox.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.logging.Logger;

import com.upstox.model.OHLCData;

public class PacketsBlockingQueue{
    private static final Logger LOGGER = Logger.getLogger(PacketsBlockingQueue.class.getName());

    private static final AtomicInteger noOfPacketReceived = new AtomicInteger(0);
    private static final AtomicInteger noOfPacketsDelievered = new AtomicInteger(0);

    private final static int INITIALSIZE = 1_000;
    private final static ArrayBlockingQueue<OHLCData> packetsQueue = new ArrayBlockingQueue<>(INITIALSIZE);

    public static OHLCData read(){
	try{
	    OHLCData ohlcData = packetsQueue.take();
	    //LOGGER.info("Delievered Packet " + ohlcData + ", total no of packets delivered " + noOfPacketsDelievered.incrementAndGet());
            return ohlcData;
	}catch(InterruptedException ex){
	    LOGGER.info("Interrupted Exception " + ex.getMessage());
	}

	return null;
    }

    public static void write(final OHLCData ohlcData){ 
	try{
            packetsQueue.put(ohlcData);
            LOGGER.info("Received a new packet " + ohlcData + ", total no of packets received " + noOfPacketReceived.incrementAndGet());
	}catch(InterruptedException ex){
            LOGGER.info("Interrupted Exception " + ex.getMessage());
	}
    }
}
