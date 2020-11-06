package com.upstox.queue;

import com.upstox.model.OHLCData;
import java.util.ArrayBlockingQueue;

public class OHLCBarDataQueue{
    //TODO : add loggers in all the java file
    //TODO : add code to convert data from model to json
    //TODO : run code with loggr to print the data with regular interval
    private static final INITIALSIZE = 10_000;
    private static final ArrayBlockingQueue<OHLCData> ohlcBarData = new ArrayBlockingQueue(INITIALSIZE);

    public OHLCData read(){
        //convert to json before returning it to the user

    }

    public void write(final OHLCData ohlcData){
        ohlcBarData.add(ohlcData);
    }
}
