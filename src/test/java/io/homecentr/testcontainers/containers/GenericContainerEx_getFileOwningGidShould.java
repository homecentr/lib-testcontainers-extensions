package io.homecentr.testcontainers.containers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class GenericContainerEx_getFileOwningGidShould {
    private static final Logger logger = LoggerFactory.getLogger(GenericContainerEx_getProcessGidShould.class);

    private GenericContainerEx _container;

    @Before
    public void before() throws IOException, InterruptedException {
        _container = new GenericContainerEx<>("centos").withCommand("bash", "-c", "sleep 100s");
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.executeShellCommand("groupadd -g 1100 test");
    }

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void returnFileOwnerUid() throws IOException, InterruptedException {
        _container.executeShellCommand("echo Hello > /tmp/out.txt");
        _container.executeShellCommand("chgrp 1000 /tmp/out.txt");

        Integer ownerUid = _container.getFileOwningGid("/tmp/out.txt");

        assertEquals(1000, (long)ownerUid);
    }

    @Test
    public void throwFileNotFoundException_GivenNonExistingPath() {
        assertThrows(FileNotFoundException.class, () -> _container.getFileOwningGid("/tmp/out.txt"));
    }
}
