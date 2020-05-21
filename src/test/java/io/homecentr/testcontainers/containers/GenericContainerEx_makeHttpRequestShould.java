package io.homecentr.testcontainers.containers;

import io.homecentr.testcontainers.SslVerification;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class GenericContainerEx_makeHttpRequestShould {
    private static GenericContainerEx _nginxContainer;

    @BeforeClass
    public static void before() throws KeyManagementException, NoSuchAlgorithmException {
        _nginxContainer = new GenericContainerEx("nginx")
            .withRelativeFileSystemBind(Paths.get( "nginx", "nginx.conf"), "/etc/nginx/conf.d/default.conf")
            .withRelativeFileSystemBind(Paths.get("nginx", "localhost.crt"), "/etc/ssl/certs/localhost.crt")
            .withRelativeFileSystemBind(Paths.get("nginx", "localhost.key"), "/etc/ssl/private/localhost.key");

        _nginxContainer.addExposedPort(443);

        _nginxContainer.start();

        SslVerification.disable();
    }

    @AfterClass
    public static void after() {
        _nginxContainer.close();
    }

    @Test
    public void returnResponseContent() throws IOException {
        HttpResponse response =  _nginxContainer.makeHttpRequest(80, "/200");

        assertEquals("Hello, world!", response.getResponseContent());
    }

    @Test
    public void returnResponseStatus() throws IOException {
        HttpResponse response =  _nginxContainer.makeHttpRequest(80, "/500");

        assertEquals(500, response.getResponseCode());
    }

    @Test
    public void supportHttps() throws IOException {
        HttpResponse response =  _nginxContainer.makeHttpRequest("https", 443, "/200");

        assertEquals("Hello, world!", response.getResponseContent());
    }
}
