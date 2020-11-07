package com.upstox.consumer;

public class FiniteStateMachine implements Runnable{
    private void consume(){
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
        while(true){ consume(); }
    }
}
