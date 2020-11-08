package com.upstox.task;

import java.util.TimerTask;
import java.util.function.Consumer;

import com.upstox.model.OHLCData;

public class OHLCPushTask extends TimerTask{

    private final OHLCData ohlcData;
    private final Consumer<OHLCData> consumer;

    public OHLCPushTask(final OHLCData ohlcData, final Consumer<OHLCData> consumer){
        this.ohlcData = ohlcData;
	this.consumer = consumer;
    }

    @Override
    public void run(){
        consumer.accept(ohlcData);
    }
    
}
