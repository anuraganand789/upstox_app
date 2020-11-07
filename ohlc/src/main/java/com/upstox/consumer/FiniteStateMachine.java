package com.upstox.consumer;

import com.upstox.model.OHLCData;

public class FiniteStateMachine implements Runnable{
    private void consume(){
	OHLCData ohlcData;
        while(true){
	   ohlcData = PacketsBlockingQueue.read(); 
	}
    }
    public OHLCData generateIntervalData(){
	computeDataPer15Seconds();
	constructBarChartData();
        return null;
    }

    private void computeDataPer15Seconds(){
    }

    private void constructBarChartData(){
    }

    @Override
    public void run(){
        consume();
    }
}
