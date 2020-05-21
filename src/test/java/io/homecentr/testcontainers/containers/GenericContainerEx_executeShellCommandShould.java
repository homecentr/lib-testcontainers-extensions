package io.homecentr.testcontainers.containers;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GenericContainerEx_executeShellCommandShould {

    private static final Logger logger = LoggerFactory.getLogger(GenericContainerEx_getProcessGidShould.class);

    private GenericContainerEx _container;

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void executeCommandUsingBashWhenRunningInBashBasedContainer() throws IOException, InterruptedException {
        _container = new GenericContainerEx<>("centos").withCommand("bash", "-c", "sleep 100s");
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.executeShellCommand("echo Hello > /tmp/out.txt");
        Container.ExecResult result = _container.executeShellCommand("cat /tmp/out.txt");

        assertEquals("Hello", result.getStdout().trim());
    }

    @Test
    public void executeCommandUsingBashWhenRunningInAshBasedContainer() throws IOException, InterruptedException {
        _container = new GenericContainerEx<>("alpine").withCommand("ash", "-c", "sleep 100");
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.executeShellCommand("echo Hello > /tmp/out.txt");
        Container.ExecResult result = _container.executeShellCommand("cat /tmp/out.txt");

        assertEquals("Hello", result.getStdout().trim());
    }
}