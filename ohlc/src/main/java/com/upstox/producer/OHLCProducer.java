package com.upstox.producer;

import java.util.List;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Timer;

import java.time.temporal.ChronoUnit;

import org.json.JSONObject;

import com.upstox.queue.PacketsBlockingQueue;
import com.upstox.model.OHLCData;
import com.upstox.task.OHLCPushTask;

import static com.upstox.util.TimeUtils.calculateTheTick;

/**
*  Reads data from Json file and produces OHLCData packets
*/
public class OHLCProducer implements Runnable{

    private static final Logger LOGGER            = Logger.getLogger(OHLCProducer.class.getName());
    private static final Timer  ohlcProducerTimer = new Timer("OHLCProducerTaskExecutor");

    private long   lastOHLCDataTimestamp = -1 ;
    private long   delayInExecution = 0;

    /** 
    *  Loads json file from resource folder and opens a input stream.
    */
    private InputStream getJsonInputStream() throws IOException{
	final String resourceName = "trades.json";
	final InputStream ios = getClass().getClassLoader().getResourceAsStream(resourceName);
	LOGGER.info("Number of kilo bytes that can be read " + ios.available() / 1024 + " kB");
        return ios;
    }

    /**
    *   Reads data from json file converts to OHCL data and pushes to OHLC-data queue
    */
    private void produce(){

	try(  InputStream ioStream          = getJsonInputStream();
	      InputStreamReader ioReader    = new InputStreamReader(ioStream);
	      BufferedReader bufferedReader = new BufferedReader(ioReader)
	    ){

            String currentLine;
            JSONObject jsonObject;

	    while((currentLine = bufferedReader.readLine()) != null){
		jsonObject = new JSONObject(currentLine);
	        addToQueue(jsonToOHLCData(jsonObject));
	    }

	}catch(IOException ioe) {
	    LOGGER.log(Level.SEVERE, ioe.getMessage());
	    ioe.printStackTrace();
	}
    }
    
    /**
    *  Returns a OHLCData packet after reading data from jsonObject
    *  @param jsonObject representation of single trade data
    */
    private OHLCData jsonToOHLCData(final JSONObject jsonObject){
        return new OHLCData(
		             jsonObject.getString("sym"), 
	                     jsonObject.getDouble("P"),
	                     jsonObject.getDouble("Q"), 
			     jsonObject.getLong("TS2")
			   ); 
    }

    /**
    *  Creates a timertask submits that data to TimerTask to be executed after calculated delay.
    *  Delay is calculated from timestamp of the first trade
    *  @param data a OHLCData packet
    */
    private void addToQueue(final OHLCData data){
        final long currentOHLCDataTimestamp = data.getTimestampUTC();

        if(lastOHLCDataTimestamp == -1) { 
            lastOHLCDataTimestamp = currentOHLCDataTimestamp; 
        } else {
            delayInExecution       +=  calculateTheTick(currentOHLCDataTimestamp, lastOHLCDataTimestamp, ChronoUnit.MILLIS);
        }

        ohlcProducerTimer.schedule(new OHLCPushTask(data, PacketsBlockingQueue::write), delayInExecution);
    }

    public static void main(final String ... args){
       new Thread(new OHLCProducer()).start(); 
    }

    @Override public void run(){ produce(); }
}
