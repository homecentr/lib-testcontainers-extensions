package io.homecentr.testcontainers.containers;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GenericContainerEx_getProcessUidShould {
    private GenericContainerEx _container;

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void returnUidOfProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "adduser -D -u 2000 dummy && su dummy -c 'sleep 100s'");
        _container.start();

        int uid = _container.getProcessUid("sleep 100s");

        assertEquals(2000, uid);
    }

    @Test
    public void returnZeroWhenProcessRunningAsRoot() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "sleep 100s");
        _container.start();

        int uid = _container.getProcessUid("sleep 100s");

        assertEquals(0, uid);
    }

    @Test(expected = ProcessNotFoundException.class)
    public void throwGivenNotExistingProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "su root -c 'sleep 100s'");
        _container.start();

        _container.getProcessUid("not-existing");
    }

    @Test
    public void returnResultWhenProcessNameSubstringOfAnotherProcess() throws IOException, InterruptedException, ProcessNotFoundException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "sleep 1000");
        _container.start();

        _container.execInContainer("ash", "-c", "sleep 100 &");

        int uid = _container.getProcessUid("sleep 100");

        assertEquals(0, uid);
    }
}