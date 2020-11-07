package com.upstox.queue;

import java.util.concurrent.ArrayBlockingQueue;
import com.upstox.model.OHLCData;

public class PacketsBlockingQueue{

    private final static int INITIALSIZE = 1_000;
    private final static ArrayBlockingQueue<OHLCData> packetsQueue = new ArrayBlockingQueue<>(INITIALSIZE);

    public static OHLCData read() throws InterruptedException{
        return packetsQueue.take();
    }

    public static void write(final OHLCData ohlcData) throws InterruptedException{ 
        packetsQueue.put(ohlcData);
    }
}
