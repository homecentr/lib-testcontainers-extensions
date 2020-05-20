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

        ImageTagResolver resolver = new ImageTagResolverImpl(system);

        assertEquals("prop-tag", resolver.getImageTag("fallback-value"));
    }

    @Test
    public void returnEnvVarValueWhenAvailable() {
        SystemWrapper system = mock(SystemWrapper.class);

        when(system.getProperty(anyString())).thenReturn(null);
        when(system.getenv(anyString())).thenReturn("env-var-tag");

        ImageTagResolver resolver = new ImageTagResolverImpl(system);

        assertEquals("env-var-tag", resolver.getImageTag("fallback-value"));
    }

    @Test
    public void returnFallbackValueWhenNoOtherIsAvailable() {
        SystemWrapper system = mock(SystemWrapper.class);

        when(system.getProperty(anyString())).thenReturn(null);
        when(system.getenv(anyString())).thenReturn(null);

        ImageTagResolver resolver = new ImageTagResolverImpl(system);

        assertEquals("fallback-value", resolver.getImageTag("fallback-value"));
    }
}
