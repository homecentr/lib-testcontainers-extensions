package io.homecentr.testcontainers.containers.helpers;

import io.homecentr.testcontainers.containers.GenericContainerEx;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class GenericContainerWithFixedPort<SELF extends GenericContainerWithFixedPort<SELF>> extends GenericContainerEx<SELF> {
    public GenericContainerWithFixedPort(ImageFromDockerfile image) {
        super(image);
    }

    public SELF withFixedExposedPort(int hostPort, int containerPort, InternetProtocol protocol) {
        super.addFixedExposedPort(hostPort, containerPort, protocol);

        return self();
    }
}
