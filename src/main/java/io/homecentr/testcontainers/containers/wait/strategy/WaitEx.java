package io.homecentr.testcontainers.containers.wait.strategy;

import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;

public class WaitEx extends Wait {
    public static LogMessageWaitStrategy forS6OverlayStart() {
        return new LogMessageWaitStrategy()
                .withRegEx(".*\\[services\\.d\\] done.*")
                .withTimes(1);
    }
}