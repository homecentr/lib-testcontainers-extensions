package io.homecentr.testcontainers.images;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageTagResolverShould {
    @Test
    public void returnPropertyValueWhenAvailable() {
        SystemWrapper system = mock(SystemWrapper.class);

        when(system.getProperty(anyString())).thenReturn("prop-tag");
        when(system.getenv(anyString())).thenReturn("env-var-tag");

        ImageTagResolver resolver = new EnvironmentImageTagResolver(system, "fallback-value");

        assertEquals("prop-tag", resolver.getImageTag());
    }

    @Test
    public void returnEnvVarValueWhenAvailable() {
        SystemWrapper system = mock(SystemWrapper.class);

        when(system.getProperty(anyString())).thenReturn(null);
        when(system.getenv(anyString())).thenReturn("env-var-tag");

        ImageTagResolver resolver = new EnvironmentImageTagResolver(system, "fallback-value");

        assertEquals("env-var-tag", resolver.getImageTag());
    }

    @Test
    public void returnFallbackValueWhenNoOtherIsAvailable() {
        SystemWrapper system = mock(SystemWrapper.class);

        when(system.getProperty(anyString())).thenReturn(null);
        when(system.getenv(anyString())).thenReturn(null);

        ImageTagResolver resolver = new EnvironmentImageTagResolver(system, "fallback-value");

        assertEquals("fallback-value", resolver.getImageTag());
    }
}
