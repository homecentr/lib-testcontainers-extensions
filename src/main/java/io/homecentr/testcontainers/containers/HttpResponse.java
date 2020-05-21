package io.homecentr.testcontainers.containers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

public class HttpResponse implements AutoCloseable {
    private final HttpURLConnection _connection;

    private String _responseContent = null;

    public HttpResponse(HttpURLConnection connection) {
        _connection = connection;
    }

    public int getResponseCode() throws IOException {
        return _connection.getResponseCode();
    }

    public String getResponseContent() throws IOException {
        if(_responseContent == null) {
            try (InputStreamReader inputReader = new InputStreamReader(_connection.getInputStream())) {
                try (BufferedReader reader = new BufferedReader(inputReader)) {
                    _responseContent = reader.lines().collect(Collectors.joining());
                }
            }
        }

        return _responseContent;
    }

    @Override
    public void close() {
        _connection.disconnect();
    }
}