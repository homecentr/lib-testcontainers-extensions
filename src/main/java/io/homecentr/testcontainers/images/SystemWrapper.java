package io.homecentr.testcontainers.images;

interface SystemWrapper {
    String getenv(String name);

    String getProperty(String name);
}
