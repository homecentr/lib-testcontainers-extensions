package io.homecentr.testcontainers.images;

public interface SystemWrapper {
    String getenv(String name);

    String getProperty(String name);
}
