package com.upstox.producer;

import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.json.JSONObject;
import com.upstox.model.OHLCData;

public class OHLCProducer implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(OHLCProducer.class.getName());
    private static int INITIALSIZE = 1_00_000;

    private Path getJsonPath(){
	final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	final Path pathToJsonFile = classLoader.getResource("trades.json");
	return pathToJsonFile;
    }

    private List<OHLCData> readFile(){
        final Path pathToJsonFile = getJsonPath();

	try(BufferedReader bufferedReader = Files.newBufferedReader(jsonFilePath)){

            String currentLine;

	    while((currentLine = bufferedReader.readLine()) != null){
	        addToQueue(jsonToOHLCData(new JSONObject(currentLine));
	    }

	}catch(IOException ioe) {
	    LOGGER.log(Level.SEVERE, ioe.getMessage());
	}

	return listOfOhlcData;
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
        PacketsBlockingQueue.write(data);
    }

    private void produce(){
    }

    @Override
    public void run(){
	while(true){ produce(); }
    }
}
