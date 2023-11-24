package no.hvl.past.util;



import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

public class MeasurementUtils {


    public static long measureExecution(Runnable method) {
        LocalDateTime start = LocalDateTime.now();
        method.run();
        LocalDateTime stop = LocalDateTime.now();
        Duration duration = Duration.between(start, stop);
        return duration.toMillis();
    }

    public static long measureAndLogExecution(Runnable method, Logger logger, Level logLevel, String msgPrefix) {
        LocalDateTime start = LocalDateTime.now();
        method.run();
        LocalDateTime stop = LocalDateTime.now();
        Duration duration = Duration.between(start, stop);
        logger.atLevel(logLevel).log(msgPrefix + " took" + duration.toMillis() + " ms");
        return duration.toMillis();
    }

    public static <R> R measureAndLogExecution(Supplier<R> method, Logger logger, Level logLevel, String msgPrefix) {
        LocalDateTime start = LocalDateTime.now();
        R result = method.get();
        LocalDateTime stop = LocalDateTime.now();
        Duration duration = Duration.between(start, stop);
        logger.atLevel(logLevel).log(msgPrefix + " took" + duration.toMillis() + " ms");
        return result;
    }

    public static <I, R> R measureAndLogExecution(I input, Function<I, R> method, Logger logger, Level logLevel, String msgPrefix) {
        LocalDateTime start = LocalDateTime.now();
        R result = method.apply(input);
        LocalDateTime stop = LocalDateTime.now();
        Duration duration = Duration.between(start, stop);
        logger.atLevel(logLevel).log(msgPrefix + " took" + duration.toMillis() + " ms");
        return result;
    }
}