package io.homecentr.testcontainers;

import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static io.homecentr.testcontainers.WaitLoop.waitFor;

public class WaitLoopShould {

    @Test(expected = TimeoutException.class)
    public void throwExceptionWhenConditionNotMetWithinTimeout() throws Exception {
        waitFor(Duration.ofSeconds(5), () -> false);
    }

    @Test
    public void returnWhenConditionMet() throws Exception {
        waitFor(Duration.ofSeconds(5), () -> true);
    }
}
