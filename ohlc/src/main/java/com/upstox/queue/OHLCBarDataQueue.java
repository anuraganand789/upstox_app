package com.upstox.queue;

import com.upstox.model.OHLCEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class OHLCBarDataQueue{
    private static final Logger LOGGER = Logger.getLogger(OHLCBarDataQueue.class.getName());
    private static final int INITIALSIZE = 1_000;
    private static final ArrayBlockingQueue<OHLCEvent> ohlcBarData = new ArrayBlockingQueue(INITIALSIZE);

    public static OHLCEvent read() throws InterruptedException{
        return ohlcBarData.take();
    }

    public static void write(final OHLCEvent ohlcEvent) throws InterruptedException{
	LOGGER.info(ohlcEvent.toJSONString());
        ohlcBarData.put(ohlcEvent);
    }
}
