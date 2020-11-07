package com.upstox.queue;

import java.util.queue.ArrayBlockingQueue;
import com.upstox.model.OHLCData;

public class PacketsBlockingQueue{

    private final int INITIALSIZE = 1_000;
    private final static ArrayBlockingQueue<OHLCData> packetsQueue = new ArrayBlockingQueue<>(INITIALSIZE);

    public static OHLCData read(){
        packetsQueue.get();
    }

    public static write(final OHLCData ohlcData){ 
        packetQueue.add(ohlcData);
    }
}
