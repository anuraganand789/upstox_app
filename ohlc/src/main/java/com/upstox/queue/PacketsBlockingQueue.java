package com.upstox.queue;

import java.util.queue.ArrayBlockingQueue;
import com.upstox.model.OHLCData;

public class PacketsBlockingQueue implements Runnable{

    private final int INITIALSIZE = 10_000;
    private final static ArrayBlockingQueue<OHLCData> packetsQueue = new ArrayBlockingQueue<>(INITIALSIZE);

    public static OHLCData read(){
        packetsQueue.get();
    }

    public static write(final OHLCData ohlcData){ 
        packetQueue.add(ohlcData);
    }

    @Override
    public void run(){
        while(true){
	    read();
	}
    }
}
