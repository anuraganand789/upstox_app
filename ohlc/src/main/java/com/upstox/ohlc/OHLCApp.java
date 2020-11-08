package com.upstox.ohlc;

import com.upstox.producer.OHLCProducer;
import com.upstox.consumer.FiniteStateMachine;

/**
*   Entry Point of this application
*/
public class OHLCApp{
    public static void main(final String... args){
        final OHLCProducer producer = new OHLCProducer();
	final Thread producerThread = new Thread(producer);

	final FiniteStateMachine consumer = new FiniteStateMachine();
	final Thread consumerThread       = new Thread(consumer);

	producerThread.start();
	consumerThread.start();
    }
}
