package com.upstox.producer;

import java.util.List;
import com.upstox.model.OHLCData;

public class OHLCProducer implements Runnable{
    private List<String> readFile(){
        List<String> jsonFromFile = Files.readAllLines();
	return jsonFromFile;
    }

    private void pushJsonToQueue(){
        //read line by line
	// convert to string
	//push to the queue
    }

    @Override
    public void run(){
	while(true){
            pushJsonToQueue();
	}
    }
}
