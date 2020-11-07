package com.upstox.queue;

import com.upstox.model.OHLCData;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class OHLCBarDataQueue{
    private static final Logger LOGGER = Logger.getLogger(OHLCBarDataQueue.class.getName());
    //TODO : run code with loggr to print the data with regular interval
    private static final int INITIALSIZE = 10_000;
    private static final ArrayBlockingQueue<OHLCData> ohlcBarData = new ArrayBlockingQueue(INITIALSIZE);

    public OHLCData read() throws InterruptedException{
        return ohlcBarData.take();
    }

    public void write(final OHLCData ohlcData) throws InterruptedException{
        ohlcBarData.put(ohlcData);
    }
}
