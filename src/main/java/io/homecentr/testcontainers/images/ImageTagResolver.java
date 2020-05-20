package io.homecentr.testcontainers.images;

public interface ImageTagResolver {
    String getImageTag(String fallbackValue);
}
