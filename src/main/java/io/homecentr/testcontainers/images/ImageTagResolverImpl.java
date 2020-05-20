package io.homecentr.testcontainers.images;

public class ImageTagResolverImpl implements ImageTagResolver {
    private final SystemWrapper _systemWrapper;

    protected ImageTagResolverImpl(SystemWrapper systemWrapper) {
        _systemWrapper = systemWrapper;
    }

    public ImageTagResolverImpl() {
        _systemWrapper = new SystemWrapperImpl();
    }

    public String getImageTag(String fallbackValue) {
        String property = _systemWrapper.getProperty("docker_image_tag");

        if(property != null && !property.isEmpty()) {
            return property;
        }

        String envVar = _systemWrapper.getenv("DOCKER_IMAGE_TAG");

        if(envVar != null && !envVar.isEmpty()) {
            return envVar;
        }

        return fallbackValue;
    }
}