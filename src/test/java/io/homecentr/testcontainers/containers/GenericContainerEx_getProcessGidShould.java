package io.homecentr.testcontainers.containers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GenericContainerEx_getProcessGidShould {
    private GenericContainerEx _container;

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void returnGidOfProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine")
                        .withCommand("ash", "-c",
                                "addgroup -g 3000 dummygrp && " +
                                "adduser -D -u 2000 dummy -G dummygrp && " +
                                "su dummy -c 'sleep 100s'");
        _container.start();

        int gid = _container.getProcessGid("sleep 100s");

        assertEquals(3000, gid);
    }

    @Test
    public void returnZeroWhenProcessRunningAsRoot() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "sleep 100s");
        _container.start();

        int gid = _container.getProcessGid("sleep 100s");

        assertEquals(0, gid);
    }

    @Test(expected = ProcessNotFoundException.class)
    public void throwGivenNotExistingProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "sleep 100s");
        _container.start();

        _container.getProcessGid("not-existing");
    }

    @Test
    public void returnResultWhenProcessNameSubstringOfAnotherProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "sleep 1000");
        _container.start();

        _container.execInContainer("ash", "-c", "sleep 100 &");

        int gid = _container.getProcessGid("sleep 100");

        assertEquals(0, gid);
    }
}
