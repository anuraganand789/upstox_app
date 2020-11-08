package com.upstox.util;

import java.time.Duration;

import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;

public class TimeUtils{

    public static long calculateTheTick(
                                        final long currentTimestamp, 
                                        final long lastTimestamp, 
				        final ChronoUnit temporalUnit
				       ){
        final Duration lastDuration = Duration.ofNanos(lastTimestamp); 
	final Duration currentDuration = Duration.ofNanos(currentTimestamp);
	final Duration diffInDuration = currentDuration.minus(lastDuration);
        return switch(temporalUnit) 
	             {
	                 case SECONDS -> diffInDuration.toSeconds();
		         case MILLIS  -> diffInDuration.toMillis();
		         case NANOS   -> diffInDuration.toNanos();
		         default      -> throw new IllegalArgumentException("Temporal Unit is not a Valid Temporal Unit Value");
                      };
    }

}
