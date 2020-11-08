package com.upstox.util;

import java.time.Duration;

import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;

public class TimeUtils{

    public static int calculateTheTick(
                                        final long currentTimestamp, 
                                        final long lastTimestamp, 
				        final ChronoUnit temporalUnit
				       ){
        final Duration lastDuration = Duration.ofNanos(lastTimestamp); 
	final Duration currentDuration = Duration.ofNanos(currentTimestamp);
	final Duration diffInDuration = currentDuration.minus(lastDuration);
        return switch(temporalUnit) 
	             {
	                 case SECONDS -> diffInDuration.toSecondsPart();
		         case MILLIS  -> diffInDuration.toMillisPart();
		         case NANOS   -> diffInDuration.toNanosPart();
		         default      -> throw new IllegalArgumentException("Temporal Unit is not a Valid Temporal Unit Value");
                      };
    }

}
