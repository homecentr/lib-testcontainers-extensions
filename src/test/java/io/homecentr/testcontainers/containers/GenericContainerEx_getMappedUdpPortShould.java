package io.homecentr.testcontainers.containers;

import io.homecentr.testcontainers.containers.helpers.GenericContainerWithFixedPort;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class GenericContainerEx_getMappedUdpPortShould {
    private static final Logger logger = LoggerFactory.getLogger(GenericContainerEx_getProcessGidShould.class);

    private int _randomHostPort;
    private GenericContainerWithFixedPort _container;

    @Before
    public void before(){
        _randomHostPort = new Random().nextInt(10000) + 40000;

        Path dockerFilePath = Paths.get(System.getProperty("user.dir"), "images", "Udp", "Dockerfile");

        ImageFromDockerfile image = new ImageFromDockerfile()
                .withFileFromPath("Dockerfile", dockerFilePath);

        _container = new GenericContainerWithFixedPort<>(image)
                .withCommand("ash", "-c", "echo Hello && sleep 10000000s")
                .withFixedExposedPort(_randomHostPort, 1234, InternetProtocol.UDP)
                .waitingFor(Wait.forLogMessage(".*", 1));
    }

    @After
    public void after() {
        _container.close();
    }

    @Test
    public void returnMappedPort(){
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        assertEquals(_randomHostPort, _container.getMappedUdpPort(1234));
    }

    @Test(expected = IllegalStateException.class)
    public void throwWhenContainerNotStarted(){
        _container.getMappedUdpPort(1234);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwGivenNegativePortNumber(){
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.getMappedUdpPort(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwGivenPortNumberHigherThanMax(){
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.getMappedUdpPort(65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwGivenPortWhichIsNotMapped(){
        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));

        _container.getMappedUdpPort(8080);
    }
}
