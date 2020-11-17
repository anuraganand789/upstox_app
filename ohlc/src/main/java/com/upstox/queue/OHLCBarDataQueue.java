package com.upstox.queue;

import com.upstox.model.event.TickEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

public class OHLCBarDataQueue{
    private static final Logger LOGGER = Logger.getLogger(OHLCBarDataQueue.class.getName());
    /** 
    *   Initial size of Priority queue.
    */
    private static final int INITIALSIZE = 1_000;
    /**
    *  A priority Blocking queue, which stores the TickEvent
    */

    private static final PriorityBlockingQueue<TickEvent> ohlcBarData = new PriorityBlockingQueue<>(INITIALSIZE);

    public static TickEvent read() throws InterruptedException{
        return ohlcBarData.take();
    }

    public static void write(final TickEvent ohlcEvent) throws InterruptedException{
        ohlcBarData.put(ohlcEvent);
    }
}
