package io.homecentr.testcontainers.containers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GenericContainerEx_getProcessUidShould {
    private static final Logger logger = LoggerFactory.getLogger(GenericContainerEx_getProcessUidShould.class);

    private GenericContainerEx _container;

    @Before
    public void before() {
        _container = new GenericContainerEx<>("alpine")
                .withCommand("ash", "-c", "adduser -D -u 2000 dummy && su dummy -c 'sleep 100s'");

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void returnUidOfProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        int uid = _container.getProcessUid("sleep 100s");

        assertEquals(2000, uid);
    }

    @Test(expected = ProcessNotFoundException.class)
    public void throwGivenNotExistingProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container.getProcessUid("not-existing");
    }
}