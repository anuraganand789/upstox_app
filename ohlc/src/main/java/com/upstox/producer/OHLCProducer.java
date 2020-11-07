package com.upstox.producer;

import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.URISyntaxException;

import org.json.JSONObject;

import com.upstox.queue.PacketsBlockingQueue;
import com.upstox.model.OHLCData;

public class OHLCProducer implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(OHLCProducer.class.getName());

    private Path getJsonPath() throws URISyntaxException{
	final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	final Path pathToJsonFile = Path.of(classLoader.getResource("trades.json").toURI());
	return pathToJsonFile;
    }

    private void readFile() throws URISyntaxException{
        final Path pathToJsonFile = getJsonPath();

	try(BufferedReader bufferedReader = Files.newBufferedReader(pathToJsonFile)){

            String currentLine;

	    while((currentLine = bufferedReader.readLine()) != null){
	        addToQueue(jsonToOHLCData(new JSONObject(currentLine)));
	    }

	}catch(IOException ioe) {
	    LOGGER.log(Level.SEVERE, ioe.getMessage());
	}
    }
    
    private OHLCData jsonToOHLCData(final JSONObject jsonObject){
        return new OHLCData(
		             jsonObject.getString("sym"), 
	                     jsonObject.getDouble("P"),
	                     jsonObject.getDouble("Q"), 
			     jsonObject.getLong("TS2")
			   ); 
    }

    private void addToQueue(final OHLCData data){
	try{
            PacketsBlockingQueue.write(data);
	}catch(InterruptedException ex){
	    LOGGER.log(Level.SEVERE, ex.getMessage());
	}
    }

    private void produce(){
    }

    @Override
    public void run(){
	while(true){ produce(); }
    }
}
