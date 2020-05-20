package io.homecentr.testcontainers.images;

public class SystemWrapperImpl implements SystemWrapper {
    @Override
    public String getenv(String name) {
        return System.getenv(name);
    }

    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }
}
