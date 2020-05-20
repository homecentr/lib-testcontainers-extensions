package io.homecentr.testcontainers;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public class WaitLoop {
    public static void waitFor(Duration waitTime, Callable<Boolean> condition) throws Exception {
        waitFor(waitTime, Duration.ofSeconds(1), condition);
    }

    public static void waitFor(Duration waitTime, Duration tickTime, Callable<Boolean> condition) throws Exception {
        long timeoutExpiredMs = System.currentTimeMillis() + waitTime.toMillis();

        while(!condition.call()) {
            long waitMillis = timeoutExpiredMs - System.currentTimeMillis();

            if (waitMillis <= 0) {
                throw new TimeoutException("The condition did not return true in given wait time.");
            }

            Thread.sleep(tickTime.getSeconds());
        }
    }
}
