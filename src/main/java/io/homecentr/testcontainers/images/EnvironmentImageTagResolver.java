package io.homecentr.testcontainers.images;

public class EnvironmentImageTagResolver implements ImageTagResolver {
    private final SystemWrapper _systemWrapper;
    private final String _fallbackValue;

    protected EnvironmentImageTagResolver(SystemWrapper systemWrapper, String fallbackValue) {
        _systemWrapper = systemWrapper;
        _fallbackValue = fallbackValue;
    }

    public EnvironmentImageTagResolver(String fallbackValue) {
        _fallbackValue = fallbackValue;
        _systemWrapper = new SystemWrapperImpl();
    }

    public String getImageTag() {
        String property = _systemWrapper.getProperty("docker_image_tag");

        if(property != null && !property.isEmpty()) {
            return property;
        }

        String envVar = _systemWrapper.getenv("DOCKER_IMAGE_TAG");

        if(envVar != null && !envVar.isEmpty()) {
            return envVar;
        }

        return _fallbackValue;
    }
}